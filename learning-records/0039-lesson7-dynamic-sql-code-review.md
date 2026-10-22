# 0039 · 第7课 动态 SQL 代码体检结论（2026-10-19）

用户要求"根据第七课，检查当前代码有没有明显的 bug"。按教学约定（引导不动代码），AI 仅点评 + 给修法。

## 体检对象
- `controller/UserController.java` 的 `/search` 接口（`findByCondition`）
- `mapper/UserMapper.java` 的 `findByCondition` 方法签名
- `resources/mapper/UserMapper.xml` 的 `findByCondition` 动态 SQL

## 已确认正确（无 bug）
1. Controller `/search`：`name`、`age` 均 `required = false` → 前端可只传一个，动态 SQL 真正生效。
2. XML 参数名：`age = #{age}` 用的是 `@Param("age")` 起的名字（之前 `#{ageMin}` 的错已修复）。

## 发现的隐患与注意点
1. **命名隐患（⚠️ 建议改）**：`UserMapper.java` 中
   `List<User> findByCondition(@Param("name") String name, @Param("age") Integer ageMin);`
   Java 变量名 `ageMin` 与 `@Param("age")`、XML `#{age}` 三者不一致。能跑（MyBatis 只看 @Param 名），但正是之前 `#{ageMin}` 报错的根因类型，且实际 SQL 已改成 `age =`（精确等于），`ageMin` 语义也错。建议统一改名为 `Integer age`。
2. **concat() 可移植性（📝 建议改）**：XML 里 `concat('%',#{name},'%')` 依赖 SQLite 3.44.0+ 才有 `concat` 函数。本机 sqlite-jdbc 3.45+/3.47/3.50 可跑，但换老版本 SQLite 或换数据库会炸 `no such function: concat`。更通用写法：`'%' || #{name} || '%'`（所有版本支持）。
3. **`age =` vs `age >=` 语义（📝 确认意图）**：当前 `age = #{age}` 是精确等于；若本意是"X 岁及以上"应改回 `>=`。非 bug，属需求确认。

## 结论
当前代码在本地 SQLite 版本下**没有会导致运行报错的明显 bug**。最值得动的是第 1 条命名隐患（早统一早省心）。

## 关联记录
- 0038：@RequestParam 默认 required=true 架空动态 SQL；XML #{ageMin}→#{age} 修复。
