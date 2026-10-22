# 0009 · 第 2 课闭卷重建评测（2026-10-16）

## 任务
不打开任何文件，凭记忆完成：① 四层装配关系 ② 口述 POST 请求旅程 ③ 前端带 id 的陷阱题。

## 学习者作答
1. Controller 注入 Service（构造器在 controller）、Service 注入 Mapper（构造器在 service）、Mapper 与 Entity 间无装载关系。
2. 浏览器发 JSON → Controller(解析 JSON 参数) 根据请求类型 → 调 Service → 调 Mapper 跑 SQL → 调数据库 → 返回结果。
3. 后端接口方法没加 id 字段，前端传的 id 被丢弃，主键还是自增的值。

## 评分
- **第 1 题：满分。** 装配方向、构造器位置、Mapper/Entity 无 DI 关系全部正确。
- **第 2 题：方向全对，漏两个执行者。** ① "调 Mapper 跑 SQL"不精确——Mapper 是接口、不执行 SQL，**MyBatis** 才翻译并执行（走 JDBC）；② 返回的最后一环漏了"Controller 把对象**序列化成 JSON**"（`@RestController` 的职责）。
- **第 3 题：结论正确，机制差一口。** 结论（客户端 id 被忽略、主键自增）完全对且是安全要点；精确机制应是：Jackson 先 `setId(999)` → INSERT 不引用 id 列所以 999 不入库 → `@Options` 把真实自增 id 写回 `user.id` → 返回 `id:5`。

## 判定
**第 2 课真正掌握**（第 1 题满分、第 3 题结论对、第 2 题方向对仅差两个执行者名词）。两个细节已口头补正，记入本课档。随即开第 3 课（MyBatis CRUD 细节）。
