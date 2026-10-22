# 0021 · @Transactional 练习场景的误区（"现实中用不到 / 接口难设计"）

## 用户困惑
- 理解 `@Transactional` = 原子性（双方要么都成功、要么都失败）✓ 正确。
- 想模拟：按用户名找到两个用户，更新第一个的 age + 第二个的 email。
- 但觉得：① 接口很难设计；② 现实中貌似用不到 → 不知道该怎么练事务。

## 纠正要点
1. **"用不到"是错觉**：单条 INSERT/UPDATE 数据库本身已原子，所以"基础 CRUD"看不到事务。
   但只要一次请求要改 **≥2 处且必须保持一致**，就必须要事务。真实例子：
   - 转账：A 账户扣钱 + B 账户加钱（经典的"要么都成、要么都败"）
   - 下单：插入订单 + 扣减库存 + 插入支付记录
   - 注册：插入用户 + 插入用户资料/初始化数据（1:1 关联）
   - 批量操作：删 N 条，中途失败要全部回滚
2. **接口设计不用"逼真"**：事务只关心"一个 Service 方法里多步 DB 操作一起成败"，
   不关心更新几个字段、几张表。练习场景越简单越好——核心是"一个方法内做 ≥2 次写"。
3. **"按名字"是多余复杂度**：练习直接用 id 即可（和批量删除同一个套路 `@RequestBody List<Long>`）。
   "按名字查再改"只是把 SELECT 放进事务内，对"理解事务"没有增量价值。

## 当前项目状态（待清理）
- Controller 新增 `PUT /users/reset`：设计别扭——`@RequestBody User` + `@RequestParam("name")` 混用，
  "按名字重置"语义不清（重置谁？按名字找到的哪个？）。
- Service `resetTwoUsers(Long age, String email, Long id)`：仍是**单步**（只调一次 update），
  套 `@Transactional` 无演示价值。
- Mapper 第 35–36 行散参 `update(age,email,id)` 与第 32–33 行 `update(User)` 重载并存，职责重叠。

## 推荐练习场景（二选一）
- **批量重置（最像真实，推荐）**：`POST /users/reset` + `@RequestBody List<Long> ids`；
  Service 循环 `update` 每个（age=0、email=reset@x.com），循环中间 `throw` 看回滚。
- **组合更新（用户原想法）**：用 DTO 收 `user1Id/newAge/user2Id/newEmail`，Service 内两次 `update`。
