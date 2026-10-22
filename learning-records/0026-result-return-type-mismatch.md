# 0026 · Result<T> 返回类型不匹配（盒子 vs 内容物）

## 用户困惑
UserController 与 Result.java "类型不兼容"。用户以为 "Result 用 T 泛型，类型应由 Controller 返回数据决定，所以方法写 public User 返回 Result.success(user) 应该没问题"。

## 根因
- 报错在 UserController 第40-41行：`public User create(...)` 声明返回 `User`，但 `Result.success(user)` 返回 `Result<User>`。`Result<User>` ≠ `User`，不兼容。
- 概念误区：`Result<T>` 中 T 是 `data` 字段(盒内内容物)的类型；方法返回类型必须是整个盒子 `Result<T>`，不是 T 本身。
- "T 由返回值决定"只对一半：T 由传给 success(...) 的实参推断，但方法签名必须显式声明 `Result<具体类型>`，编译器不会把 User 自动升级成 Result<User>。

## 修正
- 方法返回类型改为 `Result<User>` 等；统一返回需 8 个接口全改成 `Result<X>`（create/findById/update→Result<User>；findAll/search→Result<List<User>>；transfer→Result<String>；delete→Result<Void> 返回 success(null)；deleteByIds→Result<Integer>）。
- Result.java 本身正确（泛型类 + success/error 静态工厂），无需改。

## 类比
User=苹果，Result<User>=装着苹果的快递盒；声明返回苹果却递出盒子 → 不匹配。
