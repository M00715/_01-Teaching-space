# 0019 · resetTwoUsers 动手任务：思路跑偏与纠正

**时间**：2026-10-17（第 4 课动手阶段）
**触发**：学习者在 `_01` 项目里尝试写第四课动手任务 `resetTwoUsers`，主动觉察"思路错得离谱"并贴代码求纠正。

## 学习者写的（真实落盘代码）
```java
// UserMapper.java
@Update("UPDATE user SET age=#{email}, email=#{email} WHERE id=#{id}")
void update(Long age, Long email);          // ← 错误重载

// UserService.java
@Transactional
public void resetTwoUsers(Long age, Long email) {
    userMapper.update(age, email);
}
```

## 错误清单（对照真实文件行号）
1. **多参数漏 `@Param`**：`update(Long age, Long email)` 两个参数却没 `@Param`，SQL 里 `#{age}`/`#{email}` 无法绑定（类比 `findByNameLike(@Param("kw") String kw)`）。
2. **WHERE 漏 `id` 参数**：SQL 有 `WHERE id=#{id}`，但方法签名没有 `id` 形参 → 要么绑不到、要么 id 为 null 导致**误更新全表**。
3. **类型错用**：`email` 是 `String`（见 `User.email`），却写成 `Long email`。
4. **SQL 笔误**：`SET age=#{email}` —— 把 email 的值赋给了 age 列（应为 `age=#{age}`）。
5. **方法重载语义混乱**：与已有的正确 `update(User user)` 并存，混淆"按对象整体更新"与"按散参更新"。
6. **`@Transactional` 套在单步调用上**：`resetTwoUsers` 只调一次 Mapper，根本没体现"多步组合"，事务失去展示价值。

## 根本思路误区
把 `resetTwoUsers`（"重置两个用户"）理解成了"传 age/email 去更新某行"。
正确含义 = **对两个不同的用户各做一次更新（两步）**，这两步必须"一起成功/一起失败"，这才需要 `@Transactional` 罩住。

## 纠正方案
- **删掉**错误的 `update(Long age, Long email)` 重载，复用已有的 `update(User user)`。
- `resetTwoUsers` 改为接收两个用户 id，内部两次调用 `userMapper.update(user)`：
```java
@Transactional
public void resetTwoUsers(Long id1, Long id2) {
    User u1 = new User(); u1.setId(id1); u1.setAge(0); u1.setEmail("reset@x.com");
    userMapper.update(u1);                       // 第一步
    User u2 = new User(); u2.setId(id2); u2.setAge(0); u2.setEmail("reset@x.com");
    userMapper.update(u2);                       // 第二步（中间抛异常→两步一起回滚）
}
```
**状态**：已讲清，待学习者确认是否由我直接落盘修正项目文件（教学上更建议他自己敲一遍加深记忆）。

## 教学判断
学习者能主动觉察"思路错得离谱"是好信号（说明在动脑而非盲抄）。本次混淆本质是"多参数传值 + 事务语义"两个概念未固化，属 ZPD 内的正常卡点，不计入能力倒退。
