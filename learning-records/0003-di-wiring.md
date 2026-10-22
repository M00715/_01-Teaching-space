# 0003 · DI 装配链条（用户提问确认）

日期：2026-10-16

## 用户已搞清的问题
- Service 层用 `@Autowired` 装配的是 Mapper 接口吗？→ 是的。
- Controller 层 `@Autowired` 的是 Mapper 还是 Service？→ Service（不是 Mapper）。

## 关键心智模型（需固化）
装配方向 = 调用方向，逐层向下：
- Controller `@Autowired` → Service（门面依赖业务）
- Service `@Autowired` → Mapper（业务依赖数据访问）
- Mapper 接口本身由 MyBatis 生成代理对象并注册为 Spring Bean，才能被注入。

`@Autowired` 是 DI 的手段，IoC 是"创建权交给容器"的思想——两者一体两面。

## 待下节课用到的衔接点
- 现代写法推荐构造器注入（而非字段 @Autowired），可在第2课骨架里直接演示。
- `@Mapper` / `@MapperScan` 如何让接口变 Bean，是第3课（MyBatis CRUD）的前置。
