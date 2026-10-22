# 0023 · 第 5 课开启：统一返回与全局异常处理

## 用户动作
用户要求"回忆第四课，讲解第五课"。已按 teach 技能产出第 5 课课件。

## 第 4 课回忆（课件顶部 3 题检索）
HikariCP 复用连接 / @Transactional 加在 Service 层 / 不加事务导致 A 改 B 没改。与第 4 课小测一致，间隔检索巩固。

## 第 5 课主题与脉络
- **痛点**：用户 UserController 返回值"七国八制"——create→User、findAll→List<User>、transfer→String、delete→void、deleteByIds→int；异常裸奔成 Spring 默认错误 JSON。
- **统一返回体 Result<T>**：code / msg / data 三字段 + success()/error() 静态工厂。
- **Controller 改造**：所有方法返回 Result<...>，用 Result.success(...) 包一层。
- **全局异常处理 @RestControllerAdvice + @ExceptionHandler**：捕获 RuntimeException（余额不足/用户不存在）→ 返回 Result.error(500, msg)，与成功同结构。
- 课件含 4 题新小测（统一返回原因 / Result 三字段 / @RestControllerAdvice 作用 / 无全局处理时默认错误 JSON）。
- 下一课预告：第 6 课 输入校验(Bean Validation) + 分页(PageHelper/Pageable) 收官。

## 产出文件
- `lessons/0005-unified-result-exception.html`（第 5 课课件，含第 4 课回忆 3 题 + 新课 + 小测 4 题）
- `reference/unified-response.md`（速查：Result 类 / Controller 改造 / @RestControllerAdvice / 常见坑）

## 判定
统一返回是"生产级规范"非 CRUD 必需，课件标注"先别慌"不强制改现有代码；动手为可选进阶。
