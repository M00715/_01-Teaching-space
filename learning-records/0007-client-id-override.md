# 0007 · 前端带 id 发请求，会覆盖后端 id 吗

日期：2026-10-16

## 疑问
`@RequestBody` 反序列化时，如果前端 JSON 带了 `id` 字段，会不会覆盖后端 `id` 字段的值？

## 结论
对象（内存里）的 `id` 字段**会被 Jackson 临时设成客户端值**；但**持久化的权威主键不受影响**。

## 逐步机制（用 POST /users 为例，body 含 `"id":999`）
1. **Jackson 反序列化**：按名字映射 → `user.setId(999)`。这一步对象里 id 确实是 999。
2. **INSERT SQL 不含 id 列**：`INSERT INTO user(name, age, email) VALUES(#{name},#{age},#{email})`，
   id 被省略，由 DB 自增生成真实主键（如 5）。客户端 999 根本没进 SQL。
3. **`@Options(useGeneratedKeys=true, keyProperty="id")`**：insert 后把 DB 生成的真实 id(5)
   写回 `user.id`，**覆盖掉 999**。
4. **返回**：JSON 里是 `{"id":5,...}`，客户端看到的是真实 id，不是 999。

→ 客户端值被"设了又丢"，无害。

## update（PUT /users/{id}）的情况
Controller 里 `user.setId(路径变量id)` 在反序列化之后执行，会**用路径 id 覆盖 body 的 id**；
UPDATE 的 `WHERE id=#{id}` 用的是路径 id。所以 body 里伪造的 id 也被忽略。
（若 controller 不显式 setId，而用 body id 去 WHERE，则可能被越权改到别的行——显式 setId 是好习惯。）

## 安全含义
当前设计（INSERT 排除 id + @Options 回填）天然免疫"客户端伪造 id"。
**若 INSERT 写成含 id 列 `VALUES(#{id},...)`，伪造 id 就会写进库** —— 属设计缺陷，要避免。

## 进阶（更规范做法）
用 **DTO** 隔离输入与实体：`CreateUserDTO`（不含 id）收请求，`User` 实体落库。
这样连"临时被设值"都不会发生，语义更清晰。
