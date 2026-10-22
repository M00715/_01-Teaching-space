# 输入校验与分页 · 速查

> 对应课件：`lessons/0006-input-validation-pagination.html`（第 6 课）

## 1. Bean Validation（入口挡脏数据）

### 1.1 先加依赖（Spring Boot 3+ 必需）
`spring-boot-starter-web` 不再包含校验，需单独引：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```
> 不引 → `@Valid` 静默失效、也不报错（最常见"校验没反应"原因）。

### 1.2 标在"入参 DTO"上，不要标在实体上
实体还用于查库回填，给查出来的对象也校验会互相打架。新建 `UserCreateRequest`：
```java
@Data
public class UserCreateRequest {
    @NotBlank(message = "姓名不能为空")      // String 非空且 trim 后非空白
    @Size(max = 20, message = "姓名最多 20 字")
    private String name;

    @Min(value = 0,  message = "年龄不能小于 0")
    @Max(value = 150, message = "年龄不能超过 150")
    private Integer age;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "余额不能为空")      // 任意类型 ≠ null
    @Min(value = 0, message = "余额不能为负")
    private Integer money;
}
```

### 1.3 注解对照
| 注解 | 适用 | 含义 |
|---|---|---|
| `@NotNull` | 任意类型 | ≠ null |
| `@NotBlank` | 仅 String | ≠ null 且 trim 后长度 > 0 |
| `@NotEmpty` | String/集合 | ≠ null 且长度/大小 > 0 |
| `@Size(min,max)` | String/集合 | 长度范围 |
| `@Min / @Max` | 数值 | 上下界 |
| `@Email` | String | 邮箱格式 |
| `@Pattern(regexp=...)` | String | 正则格式 |

> **关键**：光标 DTO 不够，**必须在 Controller 参数前加 `@Valid`** 才触发。

### 1.4 Controller 触发
```java
@PostMapping
public Result<User> create(@Valid @RequestBody UserCreateRequest req) {
    User u = new User();
    org.springframework.beans.BeanUtils.copyProperties(req, u); // DTO → 实体
    return Result.success(userService.create(u));
}
```

### 1.5 接进全局异常（呼应第 5 课）
校验失败抛 `MethodArgumentNotValidException`，在 `GlobalExceptionHandler` 加一个比 `Exception.class` 更具体的处理器：
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public Result<Void> handleValid(MethodArgumentNotValidException e) {
    String msg = e.getBindingResult().getFieldErrors().stream()
        .map(f -> f.getField() + ": " + f.getDefaultMessage())
        .collect(java.util.stream.Collectors.joining("; "));
    return Result.error(400, msg);
}
```
返回 `{"code":400,"msg":"name: 姓名不能为空","data":null}`，与成功结构一致。

## 2. 分页（手写 LIMIT/OFFSET + COUNT，最贴 MyBatis）

### 2.1 原理
- 第 `page` 页、每页 `size` 条 → `OFFSET = (page-1)*size`
- 例：第 2 页、size=10 → `LIMIT 10 OFFSET 10`（跳过前 10 条）

### 2.2 Mapper（两条 SQL）
```java
@Select("SELECT * FROM user LIMIT #{size} OFFSET #{offset}")
List<User> findByPage(@Param("size") int size, @Param("offset") int offset);

@Select("SELECT COUNT(*) FROM user")
int countAll();
```

### 2.3 页结构（包进 Result）
```java
@Data
public class PageResult<T> {
    private List<T> list;   // 当前页数据
    private long total;     // 总条数（算总页数用）
    private int page;       // 当前页
    private int size;       // 每页条数
}
```

### 2.4 Service + Controller
```java
// Service
public PageResult<User> findPage(int page, int size) {
    int offset = (page - 1) * size;
    List<User> list = userMapper.findByPage(size, offset);
    long total = userMapper.countAll();
    return new PageResult<>(list, total, page, size);
}

// Controller
@GetMapping
public Result<PageResult<User>> findAll(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size) {
    return Result.success(userService.findPage(page, size));
}
```

### 2.5 进阶替代：PageHelper
MyBatis 拦截器，一行 `PageHelper.startPage(page, size)` 自动给紧接着的查询拼 LIMIT。好处：Mapper 不改 SQL；坏处：靠线程上下文偷偷改 SQL 的黑盒。教学阶段先手写。

## 3. 常见坑
- 忘了引 `spring-boot-starter-validation` → `@Valid` 静默失效。
- DTO 标了注解但 Controller 参数没加 `@Valid` → 不校验。
- 校验注解标在**实体** `User` 上 → 查库回填的对象也被校验，互相干扰；应标在专用 DTO。
- 分页只查数据忘了 `COUNT(*)` → 前端算不出总页数。
- `OFFSET` 写成 `page*size`（应为 `(page-1)*size`）→ 第 1 页就跳过一整页。
