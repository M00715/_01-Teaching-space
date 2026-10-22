# 已建立：Controller / Service / Mapper 职责与调用方向

用户在 2026-10-16 追问中正确分辨出三层职责：Service 承载业务逻辑、Mapper 是 SQL 执行者、调用方向为 Controller → Service → Mapper → DB，并主动质疑"参数到底谁解析"。确认其分层心智模型已从基线"名词串不起"推进到"能描述调用链"。

需补的精确细节（已当面澄清）：
- 参数绑定主要发生在 **Controller**（`@RequestBody` / `@RequestParam` / 路径变量），Service 接收已解析好的 Java 对象做业务；CRUD 简单场景 Controller 常只是把对象透传给 Service，故易混。
- **Mapper 接口由用户定义（方法 ↔ SQL 的契约），真正执行 SQL 的是 MyBatis（底层 JDBC）**——MyBatis 在运行时用动态代理实现该接口。即 Service 调的是"你写的接口"，干活的是 MyBatis。

后续课程可假设此分层基础已掌握，直接从"动手写代码 / 跑通一条 CRUD"起步，不再复述概念。
