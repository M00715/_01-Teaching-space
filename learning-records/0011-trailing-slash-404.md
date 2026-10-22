# 0011 · 404 Not Found：路径模板与请求不匹配 + 尾斜杠（2026-10-16）

## 现象
`GET localhost:8081/users/search/张` → 404，`path: "/users/search/%E5%BC%A0"`。
学习者已确认"路由看着正常"且"改代码后重启了"。

## 真正原因（两个叠加）
1. **路径模板与请求对不上**：映射是 `@GetMapping("/search/")`，方法用 `@RequestParam("name")` 取值。
   `@RequestParam` 要从**查询串**取参数，正确 URL 是 `/users/search?name=张`；
   而学习者请求的 `/users/search/张` 把名字当成了**路径段**，模板里没有 `{name}`，多出的那段没人接 → 404。
2. **尾斜杠（更根本）**：注解写成 `"/search/"`（末尾多一个斜杠）。Spring Framework 6.1+（Boot 3+，本项目 Boot 4）
   **默认关闭 trailing-slash 匹配**，`/search/` 只匹配 `/search/`、不匹配 `/search`——即使查询参数写对也会 404。

## 修复
- 去掉尾斜杠：`@GetMapping("/search")`；请求改为 `GET /users/search?name=张`。
- 若想走路径风格：`@GetMapping("/search/{name}")` + `@PathVariable String name`；请求 `GET /users/search/张`。

## 知识点
- 注解里的路径**不要随手加尾斜杠**；新版 Spring 不再宽容。
- **404 只代表"没有匹配的映射"**，与编码无关。排查时对着 URL 数一数：路径段数、是否带尾斜杠、参数走 query 还是 path，
  与注解模板**逐字对齐**。
- 取值方式与 URL 形态必须配套：`@RequestParam` → `?key=value`；`@PathVariable` → `/value`；两者不能混。
