# 0040 · 异常链路的三处误解与纠偏（2026-10-20）

用户自述理解：
controller(报错) → @Valid 校验 → service 层 → @ExceptionHandler 接住 { 调 BizException.getBizCode 取错误码 } → 返回前端。
并提出三点困惑：① msg 貌似未回传前端；② service 层 throw 了，就不走 GlobalExceptionHandler 了；③ @Transactional 那段不能删，否则事务逻辑写哪。

## 三处模糊点（均已核对真实代码，确认是误解）

1. **「service throw 了就不走 handler」——方向反了（最关键）**
   真相：`throw` 是异常冒泡的**起点**，`GlobalExceptionHandler` 是冒泡的**终点**，二者是上下游不是互斥。
   链路：`throw` → JVM 栈展开冒泡 → `DispatcherServlet` → `HandlerExceptionResolver` → 匹配 `@ExceptionHandler`。
   反证：如果不 throw，方法正常返回，根本没有异常对象，handler 才会真的空转。所以「不 throw」才是不走 handler。

2. **「msg 没回传」——实际回了**
   两处赋值点：
   - `Result.error(BizCode)`：`r.msg = bizCode.getMsg();`
   - 通用 `handleBiz(Exception)`：`Result.error(500, e.getMessage())`，而 `RuntimeException("金额必须 > 0")` 的 message 会被带出。
   结论：msg 有值。用户此前怀疑未回传，是对 `Result.error(bizCode)` 内部是否取 getMsg() 不确定所致。

3. **「BizException 处理器在工作」——实际是死代码**
   `UserService.transfer` 抛的是 `IllegalArgumentException` 与 `RuntimeException`，两者都**没有专用 handler**，全部落到 `@ExceptionHandler(Exception.class)` → `code=500 + msg`。
   `@ExceptionHandler(BizException.class)` 从未被触发。根因：**抛出的异常类型 ≠ handler 声明的类型**（Spring 按类型「最具体优先」匹配，类型对不上就落到父类型 Exception）。

## 当前真实行为
transfer 三种失败分支 → 统一 `{"code":500,"msg":"...","data":null}`。
- 金额非法 → 500（语义上应为 400）
- 转出用户不存在/余额不足 → 500
- 转入用户不存在 → 500
业务码（400/1001/1002）从未生效，因为从未抛 BizException。

## 正确改法（引导，未替用户改代码）
1. `throw new RuntimeException("转出用户不存在或余额不足：" + fromName)`
   → `throw new BizException(BizCode.INSUFFICIENT_BALANCE)`（或 USER_NOT_FOUND）。
2. `throw new RuntimeException("转入用户不存在：" + toName)`
   → `throw new BizException(BizCode.USER_NOT_FOUND)`。
3. `throw new IllegalArgumentException("金额必须 > 0")`
   → 建议 `throw new BizException(BizCode.PARAM_INVALID)`（走 400 语义）；若保留 IllegalArgumentException，它会落 500，参数错返回 500 不合理。
4. **动态消息取舍**：枚举 msg 是固定文案，无法拼 `fromName`。若要带具体名字，需给 `BizException` 增加 `(BizCode, String extra)` 重载，或放弃拼名。
5. **语义拆分建议**：`reduceMoney` 返回 0 同时代表「用户不存在」和「余额不足」，建议拆成两个 BizCode（先查用户是否存在，再判断余额）。

## @Transactional 为什么不能删
- 事务边界必须在 service 的 public 方法上。Spring 靠 AOP 代理：方法进入开事务、正常返回提交、抛异常回滚。
- Controller 是 web 层，不应承载事务。
- **回滚规则**：`@Transactional` 默认只对 `RuntimeException`（unchecked）及 `Error` 回滚；checked Exception 需显式 `rollbackFor`。
- 当前抛的 `IllegalArgumentException` / `RuntimeException` / `BizException` 全是 RuntimeException 子类 → **都会回滚，事务安全**。改成 BizException 不会破坏回滚。

## 教学方法（本次可复用）
1. **先读真实代码再讲**：不空谈理论，直接定位「哪一行 throw、哪个 handler 接、类型对不对」。
2. **「扔球/接球」比喻纠反向误解**：throw 是往天上扔球，handler 是楼顶接球的人；扔了才有球可接，不扔 handler 空转。一句话纠正「throw 就不走 handler」。
3. **对立面反问**：问「如果不 throw，handler 会触发吗？」让用户自己推出正确答案，而不是直接告知。
4. **可视化类型匹配表**：列「抛出的类型 → 匹配到的 handler → 返回码」，把「死代码」显性化（本次用 show_widget 画出）。
5. **区分两层校验**：`@Valid`（controller 参数绑定期，管格式/非空）vs service 里的 if（管业务规则），两者不重叠。另注：transfer 接口**没有** @Valid，所以金额校验是手写在 service 的。
6. **只给方向不替改**（遵守 _01 引导约定）：给出「RuntimeException → BizException」的改法与取舍，让用户自己动手。

## 待确认
- 用户是否已理解（本次讲解后待其反馈；如仍有模糊，修订本记录）。

## 关联
- `lessons/0008-exception-mechanism-concepts.html`、`reference/exception-mechanism.md`（原理：为什么能兜底）
- `reference/exception-coding-pattern.md`（编码模式：BizCode + BizException + Result 重载）
