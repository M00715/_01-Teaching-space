# 0017 · 增删改都要用 @RequestBody 吗？

**时间**：2026-10-16（第 3 课复习，0016 之后）
**学习者疑问**：涉及对数据增删改操作时都要用 `@RequestBody` 吗？

## 结论
**不是。`@RequestBody` 与"增删改查"无必然绑定，只取决于"参数是否来自客户端发送的 JSON 请求体"。**

- 新增 POST：客户端发完整对象 JSON → 用 `@RequestBody`（如 `create(@RequestBody User)`）
- 修改 PUT：客户端发完整/部分对象 JSON → 用 `@RequestBody`（如 `update(@PathVariable id, @RequestBody User)`）
- 删除 DELETE：
  - 单条删 `/users/{id}`：id 在路径，无需 body → 用 `@PathVariable`，**不用** `@RequestBody`
  - 批量删 `/users/batch`：客户端发 `[1,2,3]` 数组 → 用 `@RequestBody`（更地道写法：`@RequestParam List<Long> ids` + `?ids=1,2,3`）
- 查询 GET：参数通常在 URL（`@RequestParam` / `@PathVariable`），**基本不用** `@RequestBody`（GET 带 body 不常规且有争议）

## 落盘
已在 `src/main/java/learn/springboot/controller/UserController.java` 注释中固化：
- 类级 javadoc 增加「参数来源三注解」说明块
- `create` / `update` / `deleteByIds` 标注"用 @RequestBody 接收"
- `delete`（单删）特别注明"不需要请求体，故无 @RequestBody"，与批量删除形成对比，直观回应本疑问
