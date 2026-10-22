# 0018 · 第 4 课开启：数据源、连接池与事务

**时间**：2026-10-17（距第 3 课约一天，间隔检索）
**状态变化**：2026-10-16 学习者曾要求"先不开第 4 课"，今日主动要求"回忆第三课、开启第四课"——标志第三课 CRUD 核心已达"能动手写"基线，进入下一 ZPD。

## 第三课达成确认
- 学习者已能在 `_01` 独立写出：注解式 CRUD、模糊查询（`@Param` + `LIKE`）、XML `<foreach>` 批量删除、`@RequestBody`/`@RequestParam`/`@PathVariable` 三注解区分。
- 遗留非阻塞项：自动化测试（`@SpringBootTest`）仍按 MISSION.md 延后，不影响 CRUD 目标。

## 第 4 课主题（承接第 3 课 footer 预告）
**数据源、连接池与事务**：
1. DataSource / HikariCP 连接池——`application.yaml` 的 `url/user/password` 如何变成可复用连接（借还模型）。
2. `@Transactional` 事务——原子性、为什么开在 Service 层（业务组合处）、不加事务导致数据不一致的风险。
3. 动手（可选进阶）：给 `UserService` 加组合方法并标 `@Transactional`，故意抛异常验证回滚。

## 教学判断
- 事务对"基础 CRUD"属<b>加分认知</b>非必需；本课定位为"建立认知 + 知道何时该用"，不强制动手。
- 第 3 课 footer 已预告此主题，保持课程连贯性，未另起方向。
- 课件顶部保留"第三课回忆"3 题检索块（间隔检索），底部预告第 5 课：统一返回 `Result` + 全局异常处理 `@RestControllerAdvice`。

## 落盘
- 新增课件：`lessons/0004-datasource-transaction.html`（含回忆块 + 新课 + 6 题小测）。
