# 0025 · @RestControllerAdvice 作用范围澄清

## 用户困惑
问"加 @RestControllerAdvice 后，程序抛出的所有异常都会自动被这个注解捕获吗？"

## 纠正
1. **@RestControllerAdvice 本身不捕获异常**：它只是把类登记为全局顾问（= @ControllerAdvice + @ResponseBody，返回 JSON）。真正捕获的是内部 `@ExceptionHandler(X.class)` 方法，只接 X 类及其子类。
2. 只匹配声明的异常类型：`@ExceptionHandler(RuntimeException.class)` 接 RuntimeException 及其子类（余额不足/NullPointer/IllegalArgument）。不匹配的（如受检异常 IOException）漏过去 → Spring 默认错误 JSON。
3. 以下不在其管辖：局部 try/catch 已吞掉的异常；应用启动期异常；后台线程/定时任务异常（@ExceptionAdvice 只在一次 HTTP 请求链路生效）。
4. 想"通吃"加 `@ExceptionHandler(Exception.class)` 兜底（Exception 是根）。
5. 匹配规则：最具体的异常类型优先；兜底 Exception 放最后。
6. 生产标准写法：具体异常 handler（如 IllegalArgumentException→400）+ `Exception.class` 兜底（→500）。

## 关联
- 0024 泛型静态工厂；0023 第5课开启。本问是 0023 课件四"全局异常处理"的延伸追问。
