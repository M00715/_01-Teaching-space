# Mission: 成为 AI 应用后端开发工程师（Java 方向）

> 本 mission 于 **2026-10-22** 由原「重捡 Spring Boot CRUD」升级而来。原因：原 CRUD 主线已学完 + 验证完（见下方状态），按 `reference/ai-backend-java-roadmap.md` 的总导航，目标应扩到「大专三年竞争力底线 L2」。CRUD 不再是终点，而是地基 ①。

## Why

- 身份：**大专（专科）在校生，大一**；**专升本必考**（已定）。
- 目标职业：**AI 应用后端开发工程师（Java 方向）** → 路线 Spring Boot 地基 → Spring AI → RAG → Agent。
- 兜底要求：即便升本失败也不立即失业 → 设**保命线 L0 + 竞争力底线 L2** 双保险。
- 行业判断（2026-10 联网核实）：Java + AI 复合岗是蓝海（薪资溢价 30–50%、供不应求）；**纯 CRUD 型 Java 后端是红海**，正被 AI 编码工具 + 新人双向挤压。结论：必须冲到「复合」而非停在 CRUD。

## 当前进度（2026-10-22）

| 阶段 | 状态 | 说明 |
|------|------|------|
| **① `_01` 地基（CRUD）** | ✅ **已出关** | 8 课（0001–0008）+ `learn_02` 双闸门 A（独立写）+ B（口述 4 机制）全过。 |
| **② Git / GitHub 工程素养** | 🔄 **进行中** | 第 9 课已建（`lessons/0009-git-basics.html`）。用户已在 `learn_02` 手动实操（commit/amend/强推），正把 `_01` 与 `learn_02` 推送上 GitHub。 |
| **③ Docker 基础** | ⏳ 未开始 | 把 `_01` 容器化跑起来。 |
| **④ L1 Spring AI** | ⏳ 未开始 | ChatClient / @Tool / Advisor。 |
| **⑤ L2 RAG + 工程素养** | ⏳ 未开始 | 切片→向量→检索→重排→生成；Git/Linux/Docker/MySQL/Redis/单测/日志；Spring Security（JWT）。 |

## Success looks like（整体出口信号）

- **L0（保命线）**：万一升本失败，也能投中小厂初级 Java 岗——`_01` CRUD 深度掌握 + Git/GitHub + Docker 基础。
- **L2（三年竞争力底线 / 硬门槛）**：独立搭一个 RAG 小项目并讲清每段；工程素养四项达标；Spring Security JWT 鉴权基础。
- 区分两类「不会」：**A 类**（记不住 API，正常，靠查）vs **B 类**（不懂机制，危险，必须治）。课程与双闸门专治 B 类。

## Path（严格顺序闸门，不可跳跃）

```
① _01 地基(CRUD)  ✓ 出关
      ▼
② Git + GitHub        （进行中：把 _01 / learn_02 push 上去）
      ▼
③ Docker 基础         （容器化 _01）
      ▼
④ L1 Spring AI        （ChatClient / @Tool / Advisor）
      ▼
⑤ L2 RAG 全链路 + 工程素养
```

## Constraints

- **引导不替改（仅 `_01` 项目）**：你写代码、AI 做 review 与思路引导；只有当你明确说「帮我改 / 直接改」时 AI 才动手改 `_01` 代码。其它项目（如 `learn_02`）不受此限。
- 偏好中文讲解；希望以「检索练习 + 间隔」方式加深长期记忆。
- Git / Docker 虽属 L2 工程素养，但作为**地基工具提前在 ②③④ 阶段学会**——它们是后面所有项目托底用的。

## Out of scope（本阶段不追 / 已处置）

- 前端页面（Thymeleaf）—— 本期只做后端 REST API。
- 自动化测试（`@SpringBootTest`）—— 对「能写 CRUD」非必需，验证靠 curl/Postman；待顺手后回补。
- **Spring Security**：原列为 out-of-scope，但职业目标需要，已**提前到 L1 之前必学入门**（JWT + 拦截器鉴权）。
- Spring Cloud / 微服务 / 消息队列 —— 属 L3 上限，暂不碰。

## 关联文件

- 总导航：`reference/ai-backend-java-roadmap.md`
- Git 参考：`reference/git-basics.md` + 第 9 课 `lessons/0009-git-basics.html`
- 课件目录：`lessons/`（0001–0009）
- 学习记录：`learning-records/`
- 约束与节奏：`reference/ai-backend-java-roadmap.md` §6
