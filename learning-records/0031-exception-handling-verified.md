# 0031 · 异常处理功能 code review（用户自查后验证）

## 用户请求
"检查我新增的异常处理功能是否正确"。

## 检查结论：基本正确，修掉 1 处错误 import
- ✅ `Result.java`：`error` 方法已改 `r.msg = msg`（0029 指出的硬编码 "error" bug 已修复，异常原因可正常返回）。
- ✅ `GlobalExceptionHandler.java`：`@ExceptionHandler(Exception.class)` 兜底所有异常，返回 `Result<Void>`，逻辑正确（handleBiz 参数写 Exception e 可跑）。
- ✅ `UserController.java`：8 接口全改 `Result<...>`，返回类型与 Result.success 对得上。
- ❌ 误加 `import org.apache.ibatis.jdbc.Null;`（Null vs null 讨论遗留，代码未用 Null，非 Java null 字面量，可能编译失败）→ 已删除该行。

## 验证
- `mvn -q compile` 通过（EXIT=0）。
- 运行时预期：正常接口返回 {"code":200,"msg":"success","data":...}；transfer 余额不足/参数非法 → handler 兜住返回 {"code":500,"msg":"...","data":null}。
- 注意：`Exception.class` 兜底是"全兜"，连 Spring 自身异常(404/坏JSON)也返回 500 Result；生产可细化(404→404,400→400)，学习项目可接受。

## 关联
- 0023 第5课开启；0024-0030 为第5课相关概念澄清。本记录标记第5课统一返回+全局异常处理实装完成并验证。
