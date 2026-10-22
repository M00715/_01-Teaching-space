# 0016 · @RequestBody 注解的位置与本质

**时间**：2026-10-16（第 3 课复习期间）
**学习者疑问**：`@DeleteMapping("/batch")` 里的 `@RequestBody` 是？它只能写在这个方法的传参里吗？

## 澄清结论
1. **`@RequestBody` 是「参数级」注解**，贴在方法参数前，含义是"把这个 HTTP 请求的请求体(body)按 JSON 反序列化成该参数对象"。
   - 批量删除 `DELETE /users/batch` + 发 `[1,2,3]` body → `@RequestBody List<Long> ids` 把 body 反序列化成列表。
2. **不是只能写在这个方法**。任何 `@RestController` 处理器方法的参数都能加 `@RequestBody`，与 HTTP 方法无关。
   - 已在 `_01` 中出现：POST `create(@RequestBody User)`（第30行）、PUT `update(... @RequestBody User)`（第57行）、DELETE `deleteByIds(@RequestBody List<Long>)`（第70行）。
3. **与方法级注解区分**：
   - `@DeleteMapping("/batch")` = 方法级（声明路由）
   - `@RequestBody` = 参数级（声明参数来源）
4. **参数来源三兄弟**：
   - `@RequestBody` → 请求体（JSON 对象/数组）
   - `@RequestParam` → 查询串（`?x=值`）
   - `@PathVariable` → 路径段（`/users/123`）
5. **进阶/地道写法**：DELETE 带 body 有兼容性争议（某些网关剥离 DELETE body）。批量删除更地道用查询参数：
   `@DeleteMapping("/batch") public int deleteByIds(@RequestParam List<Long> ids)`，
   请求 `DELETE /users/batch?ids=1,2,3`，Spring 自动逗号分隔转 List。

## 当前状态
学习者确认 CRUD 学习路径上，测试类可延后（见 0014）。本澄清属"注解位置认知"，已通过。
