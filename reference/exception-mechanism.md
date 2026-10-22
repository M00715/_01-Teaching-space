# Java 异常机制 · 速查

> 对应课件：`lessons/0008-exception-mechanism-concepts.html`（第 8 课 · 概念课）

## 1. 异常体系树

```
Throwable  （所有"可抛"的根）
├── Error          严重到 JVM 级（如 OutOfMemoryError），基本不该 catch
└── Exception      程序级、可处理的意外
    ├── RuntimeException 及其子类  →  unchecked 非受检（编译不强制处理）
    │     ├── NullPointerException
    │     ├── IllegalArgumentException
    │     └── IndexOutOfBoundsException
    └── 其它 Exception（IOException、SQLException…） →  checked 受检（编译强制处理）
```

**判断口诀**：它是不是 `RuntimeException` 的子类？
- 是 → **unchecked（非受检）**：编译器不强制 try-catch / throws。
- 不是 → **checked（受检）**：编译器强制处理，否则编译不过。
- "受检"= 编译器会 **check** 你有没有处理它。

## 2. 三件套

| 关键字 | 写哪 | 作用 |
|---|---|---|
| `throw new XxxException("msg")` | 方法体里 | **主动抛**一个异常对象出去 |
| `void f() throws IOException` | 方法签名上 | **声明**会抛，把处理责任交给调用方 |
| `try {…} catch (Xxx e) {…} finally {…}` | 就地 | **自己接**；`finally` 无论成败都执行（关资源） |

## 3. 异常冒泡（核心机制）

方法 A 调 B、B 调 C：C 抛异常又没 catch → 沿 `C → B → A → …` 一层层**往回传**（栈展开 stack unwinding），直到：
- 某层 `catch` 接住 → 在此处理，冒泡停止；
- 一直没人接 → 传到 JVM → 打印堆栈、程序终止。

> 推论：Controller 方法里没写 try-catch，异常照样能被统一处理，正是因为它冒泡到了更外层的框架。

## 4. 为什么 @RestControllerAdvice 能兜住（最重要）

请求链路：
```
HTTP 请求 → DispatcherServlet → 你的 Controller 方法（抛异常·没 catch）
         → HandlerExceptionResolver（Spring 异常解析器）
         → @ExceptionHandler 你写的处理方法 → Result JSON 写回前端
```
- Controller 抛异常且没 catch → 冒泡到 `DispatcherServlet` → Spring 内置解析器交给匹配的 `@ExceptionHandler` → 你 `return Result.error(...)`，被当正常响应写回。
- 这是 **AOP / 横切** 思想：不用每个方法写 try-catch，**一处统一兜底**所有 Controller。

**最具体优先**：多个 `@ExceptionHandler` 时，Spring 选最匹配异常类型的那个。
`MethodArgumentNotValidException` 既是自身、也是 `Exception` 子类 → 校验失败被**更具体的 handleValid 先接走**，不走兜底 handleBiz。

## 5. 对照 _01 的 GlobalExceptionHandler

| 方法 | 接什么异常 | 返回 code | 语义 |
|---|---|---|---|
| `handleBiz(Exception.class)` | 兜底，其余所有 | 500 | 服务端错 |
| `handleValid(MethodArgumentNotValidException.class)` | @Valid 校验失败 | 400 | 客户端参数格式错 |
| `handleTypeMismatch(MethodArgumentTypeMismatchException.class)` | 类型转换失败（如 age=4d0） | 400 | 客户端参数类型错 |

HTTP 语义：**客户端错的给 4xx，服务端错的给 5xx**。

## 6. 实战心法：业务里也要主动 throw

```java
public User findById(Long id) {
    User u = mapper.selectById(id);
    if (u == null) {
        throw new IllegalArgumentException("用户不存在: " + id);
    }
    return u;   // 不抛只 return null，上层会一脸懵
}
```
用异常表达"业务意外"，比 `return null` 清晰——上层要么拿正常结果，要么拿统一包装的错误 JSON。

## 7. 常见坑
- 把 `NullPointerException` 当 checked 去强制 try-catch → 它其实是非受检，不必。
- 业务失败 `return null` 而不是抛异常 → 上层难判断"是真没数据还是出错了"。
- 以为 `@ExceptionHandler` 只接自己写的异常 → 框架异常（校验、类型转换）也能接，关键在"最具体优先"。
- 兜底 `handleBiz` 返回 500 却被用于参数错误 → 参数错应是 4xx，需更具体的 handler 先接。
