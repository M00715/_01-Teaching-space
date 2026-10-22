# 0029 · Result.error 的用途 与 其硬编码 msg 的 bug

## 用户困惑
"写 Result.error 方法的意义是什么？又用不到。"

## 澄清（用途）
- `Result.error` 并非死代码，主战场是 **GlobalExceptionHandler**：异常被兜住后 handler 调 `Result.error(code, msg)` 生成统一错误响应（{"code":500,"msg":"余额不足","data":null}）。
- 与 `success` 对称：success 管成功路径(code=200, Controller 调)、error 管失败路径(非200, handler 调)，两者保证成功失败都返回 Result，前端只看 code。
- 额外：Controller 也可直接 `return Result.error(400,"业务错误")` 返回业务失败而无需抛异常。
- 用户觉得"用不到"是因 handler 尚未编写。

## bug（用户 Result.java 实装）
- `error(Integer code, String msg)` 内写 `r.msg = "error";` —— 收了 msg 参数却硬编码成字面量 "error"，真实异常消息丢失。
- 应改为 `r.msg = msg;`，否则前端永远看到 msg="error" 而不知具体原因。

## 关联
- 0028 success vs 全局异常；0023 第5课开启。本记录补全 error 方法的角色与其实现坑。
