# 异常处理编码模式（错误码枚举 + 业务异常）

> 配套概念课：`exception-mechanism.md`（讲「为什么全局异常能兜底」）。
> 本文讲「怎么写」——把散落的魔法字符串/数字收拢到一处枚举，用自定义异常携带它，再统一转成 `Result`。
> 适用包：`learn.springboot.common`（与 `Result.java` 同包）。

## 1. 错误码枚举 BizCode（单一真源）

```java
package learn.springboot.common;

public enum BizCode {
    PARAM_INVALID(400, "参数非法"),
    USER_NOT_FOUND(1001, "用户不存在"),
    INSUFFICIENT_BALANCE(1002, "余额不足"),
    LOGIN_REQUIRED(1003, "请先登录");

    private final int code;
    private final String msg;

    BizCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code() { return code; }
    public String msg() { return msg; }
}
```

新增错误时**只改这一处**，全项目复用，不会出现 A 接口写「用户不存在」、B 接口写「查无此人」的不一致。

## 2. 自定义业务异常 BizException

```java
package learn.springboot.common;

public class BizException extends RuntimeException {
    private final BizCode bizCode;

    public BizException(BizCode bizCode) {
        super(bizCode.msg());   // 异常getMessage()也带消息，方便日志排查
        this.bizCode = bizCode;
    }

    public BizCode getBizCode() { return bizCode; }
}
```

## 3. Result 新增一个重载（示例，自行抄入 Result.java）

保持原有 `error(Integer, String)` 不动，新增一个收 `BizCode` 的重载：

```java
// 在 Result<T> 内，与 success / error 并列
public static <T> Result<T> error(BizCode code) {
    Result<T> r = new Result<>();
    r.code = code.code();
    r.msg = code.msg();
    return r;   // data 保持 null
}
```

## 4. 全局异常处理器新增一个方法（示例，自行抄入 GlobalExceptionHandler.java）

```java
@ExceptionHandler(BizException.class)
public Result<Void> handleBiz(BizException e) {
    BizCode c = e.getBizCode();
    return Result.error(c);   // 走第 3 步的重载
}
```

> Spring 按「最具体优先」匹配：`BizException` 比通用的 `Exception` handler 更具体，所以业务异常会进这里（返回枚举里的 code/msg），而未被捕获的其它异常仍走你原来的 `handleBiz(Exception)` → 500。

## 5. 怎么用

Service 层抛业务异常：

```java
User u = userMapper.findById(id);
if (u == null) {
    throw new BizException(BizCode.USER_NOT_FOUND);
}
if (u.getBalance() < amount) {
    throw new BizException(BizCode.INSUFFICIENT_BALANCE);
}
```

Controller 层仍可保留 `@Valid` 做参数格式校验（它兜的是「参数为空/格式错」这类程序员/调用方错误，抛 `MethodArgumentNotValidException`，已由你现有 handler 转成 400）。

## 6. 该用哪个异常（决策速查）

| 情况 | 用哪个 |
|---|---|
| 入参值非法（null/越界/负数） | `IllegalArgumentException`（标准异常） |
| 对象状态不对（未初始化就用） | `IllegalStateException`（标准异常） |
| 业务规则不成立（用户不存在、余额不足） | `BizException(BizCode.XXX)`（自定义） |

标准异常与 `BizException` 最终都转为 `Result.error` 返回前端，区别只在于：标准异常往往是**防御性/程序错误**，业务异常是**领域规则**。

## 7. 与现有 Result 的兼容

- 旧代码 `Result.error(400, "xxx")` **无需改动**，照常工作。
- 新代码统一用 `throw new BizException(BizCode.XXX)` + `Result.error(BizCode)`。
- 逐步迁移：新接口用枚举，老接口以后顺手再改。
