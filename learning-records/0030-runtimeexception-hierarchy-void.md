# 0030 · RuntimeException 异常族 与 Result<Void> 的 Void 含义

## 用户问题
1. RuntimeException 包含哪些错误类型？
2. Result<Void> 为何是 Void，Void 代表谁？

## 解答1：RuntimeException 异常族
- RuntimeException = 所有**非受检异常**(unchecked)的父类。常见子类：NullPointerException、IllegalArgumentException(参数非法)、IllegalStateException、IndexOutOfBoundsException/ArrayIndexOutOfBoundsException、ArithmeticException(除零)、ClassCastException(强转失败)、UnsupportedOperationException。
- 非受检：编译器不强制 try/catch 或 throws，Service 里直接 throw 即可。
- **不含受检异常**(checked)：IOException/SQLException 等不继承 RuntimeException，@ExceptionHandler(RuntimeException.class) 兜不住 → 需加 @ExceptionHandler(Exception.class) 兜底(Exception 是祖宗，含受检+非受检)。
- 本项目 transfer 抛的 IllegalArgumentException(金额≤0) + RuntimeException(余额不足) 都是 RuntimeException 子类 → 被 handler 兜住转 Result.error。

## 解答2：Void 含义
- Void(java.lang 类) 在 Result<Void> 中代表"无数据负载"，data=null。异常时无业务数据可返，只给 code+msg。
- void(小写)是关键字(方法无返回值)，不能作泛型参数 → 用大写 Void 类占位。成功 Result<User>(data 装用户)/Result<List<User>>(装列表)；出错 Result<Void>(data 为 null)。

## 关联
- 0029 error 方法用途+硬编码bug；0025 advice 作用范围；0023 第5课。用户 GlobalExceptionHandler 已建(只接 RuntimeException，handleBiz 参数写成 Exception e 可跑但惯例写 RuntimeException e)。
