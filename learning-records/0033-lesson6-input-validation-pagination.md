# 0033 · 第6课：输入校验与分页（收官）

## 第6课（2026-10-19）内容
- 课件：`lessons/0006-input-validation-pagination.html`（第5课回忆3题 + 新课 + 小测4题）。
- 速查：`reference/input-validation-pagination.md`。
- 主题：① Bean Validation 挡脏数据（@NotNull/@NotBlank/@Size/@Min/@Email + @Valid）；② 手写分页 LIMIT/OFFSET + COUNT，包成 PageResult<T> 再进 Result。
- 定位：第5课统一返回/全局异常的生产级延续；同样是"规范非必需"。

## 关键知识点（用户需掌握）
1. **校验依赖要单独引**：Spring Boot 3+ `spring-boot-starter-web` 已不含 validation，须加 `spring-boot-starter-validation`，否则 @Valid 静默失效。
2. **@Valid 才是开关**：DTO 标注解不够，Controller 参数前必须加 @Valid 才触发；漏了是最常见坑。
3. **标在入参 DTO 而非实体**：实体还用于查库回填，标校验会互相干扰；新建 UserCreateRequest。
4. **校验失败接全局异常**：MethodArgumentNotValidException 被 GlobalExceptionHandler 接成 Result.error(400, 字段错误)，和第5课同一结构——这正是统一返回的回报。
5. **分页两样东西**：当前页数据(LIMIT/OFFSET) + 总条数(COUNT(*))；OFFSET=(page-1)*size。PageResult<T>{list,total,page,size} 包进 Result<PageResult<T>>。
6. **PageHelper 进阶替代**：一行 startPage 自动拼 LIMIT，黑盒；教学先手写。

## 与项目现状衔接
- `_01` 当前 `create(@RequestBody User user)` 无校验、`findAll()` 不分页。第6课给出改法：引依赖→建 UserCreateRequest→Controller 用 @Valid→GlobalExceptionHandler 加 MethodArgumentNotValidException 处理器→Mapper 加 findByPage/countAll→建 PageResult→findAll 收 page/size。
- 用户决定：先看课件，未要求当场实装（沿用第5课"动手可选"模式）。

## 下一步
- 6 课全部讲完。建议结课作业：挑一张自己的表，从零写一遍"请求链路 + CRUD + 统一返回 + 全局异常 + 校验 + 分页"全流程。
