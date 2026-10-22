# 0010 · 模糊查询的路径冲突（Ambiguous handler methods）（2026-10-16）

## 现象
`GET localhost:8081/users/张` → 500，报错：
`java.lang.IllegalStateException: Ambiguous handler methods mapped for '/users/%E5%BC%A0'`
响应体 `path: "/users/%E5%BC%A0"`。

## 学习者推测
以为是"文字编码相关的问题"。

## 真正的根因（与编码无关）
Controller 新增的模糊查询用了 `@GetMapping("/{name}")`，与已有的 `@GetMapping("/{id}")` **路径形状相同**（都是 `/users/{一段变量}`）。
Spring 匹配请求时发现两个 HandlerMethod 都匹配 `/users/张`，无法抉择 → 抛 `Ambiguous handler methods mapped`（500）。
- 该错误对任意 `/users/xxx`（含 `/users/1`）都会发生，与中文无关。
- URL 里的 `%E5%BC%A0` 只是"张"的 UTF-8 百分号编码（E5 BC A0），是**干扰项**，非乱码、非根因。

## 附带发现的第二个 bug
`UserService.findByNameLike` 写成 `return (User) userMapper.findByNameLike(name);`，但 Mapper 返回 `List<User>`。
因 `User` 非 final，编译期允许该 cast，**运行期会抛 `ClassCastException`**。
正确：Service/Controller 的 `findByNameLike` 返回类型都应为 `List<User>`，不做强转。

## 修复
- Controller：改 `@GetMapping("/search")` + `@RequestParam("name") String name`，返回 `List<User>`；请求改为 `GET /users/search?name=张`。
- Service：返回类型改回 `List<User>`，去掉 `(User)` 强转。

## 知识点
1. 路径变量 `{x}` 与 `{y}` 形状相同 → 冲突。区分办法：加一段**固定路径**（`/search`、`/name/{name}`）或改用 `@RequestParam`。
2. 返回类型必须与 Mapper 一致；多结果数据一律用 `List`/集合，不能强转成单个对象。
3. URL 中的中文路径会显示为百分号编码，属正常现象，排查时不要被它带偏——先看**异常类型**（Ambiguous / 415 / 400 / 405）。
