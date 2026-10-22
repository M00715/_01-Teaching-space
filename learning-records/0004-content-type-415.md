# 0004 · 415 Unsupported Media Type（Content-Type 不匹配）

日期：2026-10-16

## 现象
Postman POST `localhost:8080/users`，Body 选 **form-data**，返回：
```
415 Unsupported Media Type   {"status":415,"error":"Unsupported Media Type","path":"/users"}
```

## 根因
- Controller 方法签名：`create(@RequestBody User user)`。
- `@RequestBody` 的语义 = 「把 HTTP 请求体按 **JSON** 反序列化成 User 对象」，它要求的
  `Content-Type` 是 `application/json`。
- 而 Postman 的 `form-data` 发的是 `multipart/form-data`，Spring 无法用 `@RequestBody` 解析 → 415。

## 修复
Postman → Body → 选 **raw** → 选 **JSON**，填：
```json
{"name":"张三","email":"z@s.com"}
```
不要填 `id`（自增主键由数据库生成，插入后回填）。

## 需固化的对比（易混）
| 想接收的请求体形态 | 服务端注解 | Content-Type |
|---|---|---|
| JSON 体 | `@RequestBody` | application/json |
| 表单字段 | `@RequestParam` / `@ModelAttribute` | application/x-www-form-urlencoded 或 multipart/form-data |
| URL 路径段 | `@PathVariable` | — |
| URL 查询串 | `@RequestParam` | — |

415 ≈ "你发的内容类型我不支持"；400 ≈ "内容类型对但内容本身有问题"；405 ≈ "路径对了但 HTTP 方法不对"。
