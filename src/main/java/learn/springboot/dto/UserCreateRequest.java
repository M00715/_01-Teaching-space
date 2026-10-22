package learn.springboot.dto;

import jakarta.validation.constraints.*;

public class UserCreateRequest {
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "姓名长度必须在 {min} 到 {max} 之间")
    private String name;

    @NotNull(message = "年龄不能为空")
    @Min(value = 4, message = "年龄不能小于 4")
    @Max(value = 130, message = "年龄不能大于 130")
    private Integer age;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "金额不能为空")
    @Min(value = 0, message = "金额不能为负")
    private Integer money;
}
