# 0020 · 据 0019 自行修改后仍偏：resetTwoUsers 仍是单步

**时间**：2026-10-17（0019 纠正后，学习者自行改代码）
**触发**：学习者按 0019 反馈修改 `_01` 项目，主动贴出改动问"Controller 接口如何设计"。

## 学习者改后的真实代码
```java
// UserMapper.java（第 35-36 行，新增散参重载）
@Update("UPDATE user SET age=#{age}, email=#{email} WHERE id=#{id}")
void update(@Param("age") Long age, @Param("email") String email, @Param("id") Long id);

// UserService.java（第 50-53 行）
@Transactional
public void resetTwoUsers(Long age, String email, Long id) {
    userMapper.update(age, email, id);
}
```

## 进步（语法层已对）
- 加了 `@Param("age"/"email"/"id")`，参数能正确绑定。
- `email` 从 `Long` 改为 `String`（与 `User.email` 一致）。
- SQL 修正为 `age=#{age}, email=#{email}, id=#{id}`，无笔误、WHERE 含 id。

## 仍存在的设计层问题（非编译错）
1. **resetTwoUsers 仍是单步**：只调一次 `userMapper.update(...)`，本质是"用 age/email 更新**某一个** id 的记录"，与"重置两个用户"无关。`@Transactional` 套在单步上无展示价值——事务要罩住"多步"。
2. **冗余重载**：新增 `update(Long,String,Long)` 与既有 `update(User user)` 并存。MyBatis 不推荐 Mapper 接口重载（按方法名映射易歧义），且两 update 职责重叠。

## 根因（与 0019 同）
仍未理解 `resetTwoUsers` 语义 = "对**两个（多个）不同用户**各做一次更新（两步）"，而非"更新单条记录的 age+email 字段"。

## 纠正路线（本次讲解）
- 删掉散参 `update` 重载，统一 `update(User user)`。
- `resetTwoUsers` → 泛化 `resetUsers(List<Long> ids)`，循环内对每个 id 构造 `User` 并 `userMapper.update(u)`（多步），`@Transactional` 才有意义。
- **Controller 设计**：写操作 → `POST /users/reset` + `@RequestBody List<Long> ids`（与既有 `DELETE /users/batch` 同套路）。
  - `/reset` 固定段，避开 `GET /users/{id}` 冲突（不写 `/{ids}` 占位形状）。
  - 不加尾斜杠（防新版 Spring 404）。
  - 返回 `void`（第 5 课再包 `Result`）。
- 验证回滚：循环第二步前抛异常，调 `[1,2]` 看两用户都"没变"。

## 状态
讲解中，待学习者确认是否由我一次性落盘：删 Mapper 重载 + 改 Service 为多步 + 加 Controller 端点。
