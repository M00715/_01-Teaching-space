# 0038 · @RequestParam 默认必填 + XML 参数名 bug（第7课实装排查）

## 背景
用户自己诊断出 `/users/search` 接口问题：前端不传 name/age 会报错，且认为这破坏了动态 SQL 的意义。

## 结论
1. **`@RequestParam` 默认 `required=true`**：`@RequestParam("name")` 是 `@RequestParam(value="name")` 简写，未写 `required` 即必填。前端只传一个参数 → Spring 返 400 `Required request parameter 'age' ... is not present`。这从入口强制参数齐全，架空了 XML 里 `<if>` 的动态性。
   - 修复：`@RequestParam(value="name", required=false) String name` + age 同理。不传时为 `null`，XML `<if>` 跳过 → 真动态。
   - 注意：`required=false` 配 `Integer`（包装类），不能用 `int`（基本类型无法收 null）。

2. **XML `#{ageMin}` bug**：接口 `findByCondition(@Param("name") String name, @Param("age") Integer ageMin)`。MyBatis 只认 `@Param` 名 `age`，不认 Java 变量名 `ageMin`。`test="age != null"` 对（用 @Param 名），但 `#{ageMin}` 引用不存在的名字 → 运行抛 `BindingException: Parameter 'ageMin' not found`。
   - 修复：改为 `#{age}`。`test` 与 `#{}` 必须都用同一个 `@Param` 名。

## 改动文件
- `controller/UserController.java`：`/search` 两参数加 `required=false`（附中文注释说明）。
- `resources/mapper/UserMapper.xml`：`#{ageMin}` → `#{age}`。
- `mvn -q compile` 通过 EXIT=0。

## 修复后 /search 四种行为
- `?name=张` → `WHERE name LIKE '%张%'`
- `?age=20` → `WHERE age >= 20`
- `?name=张&age=20` → 两者 AND
- 都不传 → `<where>` 消失 → `SELECT * FROM user`（查全部）
