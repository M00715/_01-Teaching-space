# 统一返回与全局异常处理 · 速查

> 对应课件：`lessons/0005-unified-result-exception.html`（第 5 课）

## 1. 为什么需要

- 接口返回值"七国八制"：实体 / 列表 / 字符串 / void / int 混用，前端每接一个接口换一种解析。
- 异常"裸奔"：Service 抛异常时 Spring 返回默认错误 JSON（`timestamp/status/error/path/message`），和成功结构不一致。
- 目标：**所有接口成功失败都返回同一种结构** `{ code, msg, data }`。

## 2. Result&lt;T&gt; 返回体

```java
@Data
public class Result<T> {
    private int code;     // 业务码：200=成功，非200=失败（不是 HTTP 码）
    private String msg;   // 提示：成功 "ok"，失败具体原因
    private T data;       // 业务数据，失败可为 null

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200; r.msg = "ok"; r.data = data;
        return r;
    }
    public static <T> Result<T> error(int code, String msg) {
        Result<T> r = new Result<>();
        r.code = code; r.msg = msg;   // data 保持 null
        return r;
    }
}
```
- 约定：`code=200` 业务成功；失败用 `400/500` 等语义（如余额不足 = 400）。
- 泛型 `T`：成功时放任意数据（User / List / 甚至 String "转账成功"）；`delete` 返回 `Result<Void>` 或 `Result<String>`。

## 3. Controller 改造

```java
@PostMapping
public Result<User> create(@RequestBody User user) {
    return Result.success(userService.create(user));
}

@PostMapping("/transfer")
public Result<String> transfer(@RequestBody TransferRequest req) {
    userService.transfer(req.getFromName(), req.getToName(), req.getAmount());
    return Result.success("转账成功");
}
```
所有方法签名统一为 `Result<...>`，前端一套解析：`if (code==200) 用 data; else 弹 msg;`。

## 4. 全局异常处理 @RestControllerAdvice

```java
@RestControllerAdvice   // = @ControllerAdvice + @ResponseBody，全局增强所有 @RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)   // 捕获指定异常（含转账的余额不足/用户不存在）
    public Result<Void> handleBiz(RuntimeException e) {
        return Result.error(500, e.getMessage());
    }
}
```
- `@ControllerAdvice`：全局增强，里写的方法对**所有** Controller 生效。
- `@RestControllerAdvice` = 增强 + 返回 JSON（REST 用）。
- `@ExceptionHandler(X.class)`：指定捕获哪类异常；可写多个方法分别处理不同异常（如 `IllegalArgumentException`→400，其他→500）。更具体的异常类排前面优先匹配。
- 效果：余额不足异常 → 前端拿到 `{"code":500,"msg":"余额不足","data":null}`，与成功同结构。

## 5. 常见坑

- `Result` 放哪个包：一般 `common` / `dto` 包（`learn.springboot.common`）。
- 别把 HTTP 状态码和业务 code 混为一谈：业务失败通常仍返回 HTTP 200 + `code=400`，由前端看 `code` 判断。
- 多个 `@ExceptionHandler` 时，异常类型越具体越优先；`Exception` 兜底放最后。
