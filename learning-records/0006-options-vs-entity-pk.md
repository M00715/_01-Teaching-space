# 0006 · 为什么实体类没标 @Id，insert 却能回填主键

日期：2026-10-16

## 用户疑问
`@Options(useGeneratedKeys = true, keyProperty = "id")` 看起来像在「声明 id 是主键」，
但 `User` 实体类没有任何注解标明 id 是主键，为什么？

## 澄清（四点）
1. **`@Options` 不是声明主键，是「回填指令」**：
   `useGeneratedKeys=true` 让 JDBC 在执行 INSERT 后返回数据库生成的主键；
   `keyProperty="id"` 告诉 MyBatis 把那个值塞进参数对象 `User` 的 `id` 属性。
   所以 insert 之后 `user.getId()` 就有值了。
2. **主键真正定义在数据库，不在 Java**：
   `schema.sql` 里 `id INTEGER PRIMARY KEY AUTOINCREMENT` 才是「id 是主键且自增」的真相源。
3. **MyBatis ≠ JPA/Hibernate**：
   MyBatis 是 SQL 映射框架，实体就是普通 POJO，**不靠注解维护表结构关系**；
   列 ↔ 字段默认按「名字」映射（id 列 → id 字段），所以 `User` 上不需要 `@Id`。
4. **对比 JPA**：JPA 会在实体写 `@Id` + `@GeneratedValue` 来声明并管理主键；
   MyBatis 让你自己写 SQL，用 `@Options` 取回生成键，二者设计哲学不同。

## 衔接
- 本项目用的是 MyBatis（非 MyBatis Plus，更非 JPA），所以看不到 `@Id`。
- 第 3 课（MyBatis CRUD 细节）会展开：@Options 取键、结果映射、#{} vs ${}。
