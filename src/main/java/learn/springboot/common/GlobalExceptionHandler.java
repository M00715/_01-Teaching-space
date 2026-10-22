package learn.springboot.common;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理类。
 *
 * @RestControllerAdvice 是 Spring 的"全局增强"注解：
 *   - 等价于 @ControllerAdvice + @ResponseBody；
 *   - 类里用 @ExceptionHandler 标注的方法，会对【所有 Controller】生效，
 *     不必在每个接口里各自写 try-catch；
 *   - @ResponseBody 表示异常被拦截后，返回的是 JSON（而不是跳转到错误页面），
 *     正好契合 REST API。所有返回结果都复用第 5 课的 Result<T> 统一结构。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 兜底处理器：捕获所有 Exception（及其子类）。
     *
     * @ExceptionHandler(Exception.class) 指定"捕获哪一类异常"。
     * 当某个 Controller 接口抛出异常、且没有更具体的处理器匹配时，就落到这里，
     * 统一返回 code=500 + 异常的 message。
     *
     * 注意：如果存在多个 @ExceptionHandler，Spring 按"最具体优先"匹配。
     * 例如 MethodArgumentNotValidException 既是自身类型、也是 Exception 的子类，
     * 它会被下面更具体的 handleValid 先接走，走不到这个兜底方法。
     */
    @ExceptionHandler(Exception.class) // 兜底：接住所有未被更具体处理器捕获的异常
    public Result<Void> handleBiz(Exception e) {
        // Void 在这里仅是泛型占位符，表示"这次没有业务数据要返回"，data 字段为 null
        return Result.error(500, e.getMessage());
    }

    @ExceptionHandler(BizException.class)
    public Result<Void> handleBiz(BizException e) {
        BizCode bizCode = e.getBizCode();
        return Result.error(bizCode);
    }

    /**
     * 校验失败处理器：专门接住 @Valid 触发的校验异常。
     *
     * MethodArgumentNotValidException = 当方法的参数（标注了 @Valid 的 DTO）
     * 没通过 Bean Validation 校验时，Spring 自动抛出的异常。
     * 它比 Exception 更具体，因此优先于上面的 handleBiz 被匹配。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) // 比 Exception.class 更具体，优先匹配
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        // 1) getBindingResult() 装着这次校验的全部结果，getFieldErrors() 取出【所有】出错字段。
        //    一次请求可能多个字段同时不合格，所以返回的是 List<FieldError> 而非单个错误。
        // 2) .stream() 把列表变成"流水线"，方便逐个加工（等同于 for 循环遍历）。
        // 3) .map(...) 把每个 FieldError 加工成一段文字："字段名: 错误信息"
        //      - f.getField()          → 出错字段名，例如 "name"
        //      - f.getDefaultMessage() → 你写在注解里的 message，例如 "姓名不能为空"
        // 4) .collect(Collectors.joining("; ")) 把所有文字用分号 " ; " 拼成【一条】字符串。
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));

        // 上面的 stream 写法，等价于下面的 for 循环（stream 只是它的简写）：
        // StringBuilder sb = new StringBuilder();
        // List<FieldError> errors = e.getBindingResult().getFieldErrors();
        // for (int i = 0; i < errors.size(); i++) {
        //     FieldError f = errors.get(i);
        //     if (i > 0) sb.append("; ");                              // 不是第一个就先加分号
        //     sb.append(f.getField()).append(": ").append(f.getDefaultMessage());
        // }
        // String msg = sb.toString();

        // 最终返回统一结构：code=400（请求参数有误），msg 为所有错误拼接后的可读文本。
        // 例如：仅 name 为空 → "name: 姓名不能为空"；
        //       name 空且 email 错 → "name: 姓名不能为空; email: 邮箱格式不正确"
        return Result.error(400, msg);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        Class<?> type = e.getRequiredType(); // 期望类型，如 Integer.class（一个 Class 对象）
        // getSimpleName 只取类名（干净）" Integer"
        String TypeName = (type != null) ? type.getSimpleName() : "?";
        String msg = "参数" + e.getName() + "收到非法值" + e.getValue() + "，应为 " + TypeName;
        return Result.error(400, msg);
    }
}
