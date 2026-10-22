# 0032 · 第5课收尾 + 序列化 `{}` 坑

## 第5课最终状态（已完成）
- 课件：`lessons/0005-unified-result-exception.html`（第4课回忆3题 + 新课 + 小测4题）
- 速查：`reference/unified-response.md`
- 实装三大件：`common/Result.java`、`common/GlobalExceptionHandler.java`、`controller/UserController.java`（8 接口全 `Result<...>`）
- `mvn -q compile` 通过（EXIT=0）

## 最后的坑：接口返回 `{}`（无 code）
- 现象：触发余额不足时接口返回 `{}`，无 `code`/`msg`。
- 根因：**`Result` 字段全 `private` 且类上无 getter**，Jackson 序列化时扫不到 `getCode()/getMsg()/getData()` → 认为无属性 → 输出 `{}`（成功接口其实也如此）。
- 修复：给 `Result` 加 `@Data`（含 `import lombok.Data`）。已重新编译验证 EXIT=0。
- 用户疑问"加 toString 效果一样吧"：**不对**。`toString()` 只影响日志打印（给人看），不影响 Jackson 的 JSON 输出（给 Jackson 看的是 getter）。真正修好靠的是 `@Data` 顺带生成的 getter；最小等效修复是 `@Getter`。

## 第5课核心收获（用户已打通）
1. `Result<T>` 是"统一盒子"，T 是 `data` 字段类型；方法返回类型须写整体 `Result<X>` 而非 X。
2. 静态工厂 `<T> Result<T> success(T data)` 三处 T 同名同类型，由实参推断；static 拿不到类级 T 须自声明。
3. `@RestControllerAdvice` 本身不捕获异常，靠 `@ExceptionHandler(X.class)`；`Exception.class` 兜底通吃，最具体优先。
4. 异常冒泡：Service 抛→Controller 抛→Spring 框架层；`success` 在异常后不执行；单请求失败非 JVM 崩盘。
5. `Result.error` 主战场是全局异常处理器（与 success 对称）；曾踩硬编码 `r.msg="error"` bug，已改为 `r.msg=msg`。
6. `void`(关键字) ≠ `Void`(java.lang 类，泛型空占位，唯一值 null)；`null` ≠ `Null`。

## 下一步
- 第6课预告：输入校验（Bean Validation）+ 分页。
- 用户决定第5课到此收尾，次日继续。
