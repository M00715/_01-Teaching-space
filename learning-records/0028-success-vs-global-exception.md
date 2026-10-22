# 0028 · 为什么用全局异常处理而非每接口 try-catch

## 用户洞察（关键）
用户发现：所有接口都 return Result.success(...)，若接口内 Service 抛异常，Result.success 不执行，请求失败；进而联想到"这正是让我实现全局异常处理、而非每接口 try-catch 的原因"。理解完全正确。

## 机制
- 异常沿调用栈冒泡：Service 抛 → Controller 方法跟着抛 → Spring 框架层（DispatcherServlet）。Result.success 在异常之后不执行。
- "崩盘"澄清：单个请求失败（HTTP 500），JVM 不挂、不影响其他请求。无全局处理时 Spring 返回默认错误 JSON（timestamp/status/error/path…）= "异常裸奔"。
- 每接口 try/catch 缺陷：8 接口重复样板、易漏、且局部 catch 吞异常掩盖 bug。
- @RestControllerAdvice 一处写全局生效：正常流 return Result.success；异常流由全局 handler 转 Result.error；两路皆 {code,msg,data} 同结构 → Result.error 主要给 handler 用，Controller 保持干净。
- 补充：RuntimeException handler 之外应加 @ExceptionHandler(Exception.class) 兜底（最具体优先），否则 IOException 等受检异常仍漏给默认 JSON。

## 关联
- 第5课核心：success 管正常 + advice 管异常，汇成统一 Result。0027 改造坑；0023 第5课开启。
