# 0027 · 把 Controller 改造成统一返回时的两个泛型坑

## 坑1：列表接口误用 Result<User> + 强转（用户原写）
- 错：`public Result<User> findAll()` 返回 `Result.success((User) userService.findAll())`。
- `findAll()` 返回 `List<User>`（多个），不是单个 User；`(User)` 把 List 强转成 User 是谎言：编译期 unchecked 警告通过，运行期抛 `ClassCastException: ArrayList cannot be cast to User`。
- 正：`public Result<List<User>> findAll()` + `return Result.success(userService.findAll());`（无强转，T 自动推断 List<User>）。T = data 字段类型，列表接口的 data 是 List。

## 坑2：Result<Void> 删除接口调用 Result.success() 无参（用户原写）
- 错：`return Result.success();` —— `success(T data)` 必须传参，无参编译报错。
- 概念：`void`（小写关键字=方法无返回值）≠ `Void`（大写 java.lang 类，`data` 装箱类型，唯一值 null）。`Result<Void>` = 不带数据的 Result，data 为 null。
- 正：① `return Result.success(null);`（T 推断 Void，data=null）；② 或改为 `Result<String>` 返回 `Result.success("删除成功")`（前端更友好，推荐）。

## 关联
- 0026 盒子vs内容物（方法须声明 Result<X> 而非 X）；0024 静态工厂 <T>。本记录是 0026 在"动手改造 Controller"时的具体落地错误。
- 正确映射表：create/findById/update→Result<User>；findAll/search→Result<List<User>>；transfer→Result<String>；delete→Result<Void>(success(null)) 或 Result<String>；deleteByIds→Result<Integer>。
