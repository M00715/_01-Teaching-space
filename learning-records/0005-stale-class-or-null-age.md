# 0005 · 加了 age 字段但 GET 不显示

日期：2026-10-16

## 现场
用户给 User 加了 `age` 字段（entity / mapper insert+update / schema.sql 三处都改对），
但 Postman 发 GET 不显示 age。

## 排查结论（已验证）
- `User.java` 有 `private Integer age;` ✓
- `UserMapper` insert/update 带了 age，SELECT 是 `SELECT *` ✓
- `schema.sql` 建了 age 列 ✓
- 实际 SQLite `user` 表 **已有 age 列**（PRAGMA table_info 确认）✓

→ 代码与库都对，问题在「运行中的进程」。

## 两个可能原因
1. **没重启应用（最可能）**：Spring Boot 默认不热加载源码。改了 .java 后，
   仍在跑的 `mvn spring-boot:run` 进程加载的是**旧的 User.class（无 age）**，
   Jackson 序列化时根本没有 age 这个字段 → JSON 里完全没有 age 键。
   ✅ 修复：Ctrl+C 停掉，重新 `mvn spring-boot:run`。
2. **重启了，但旧数据 age 为 NULL**：表是先建、后加的列（或列可空），
   已有行的 age 是 NULL → JSON 显示 `"age": null`。
   ✅ 修复：POST 一条**带 "age"** 的新用户，再 GET 那条就能看到真实值。

## 固化的知识点
- Spring Boot 默认无热重载；改源码必须重启（或加 `spring-boot-devtools` 实现自动重启）。
- MyBatis `SELECT *` 按「列名 → 字段名」映射；列存在就能填，列不存在 → 字段为 null。
- DB 里 NULL → JSON 里 `null`（默认 Jackson 不忽略 null）。
