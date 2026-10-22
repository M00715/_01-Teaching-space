# 0037 · 第7课正式讲解：MyBatis 动态 SQL

## 基本信息
- 时间：2026-10-19 晚。用户说"开始学第七课"，未要求换主题，沿用框架 `lessons/0007-mybatis-dynamic-sql.html`（主题：MyBatis 动态 SQL）。
- 教学形式：开场检索（回忆第6课3题并揭晓）→ 讲痛点 → 注解vsXML → `<if>`+`<where>`（配 show_widget 拼流程图）→ `<foreach>` → `<set>` → 打开课件供自测。

## 核心知识点（本课交付）
1. **为何动态 SQL**：写死 SQL 难做"可选条件组合"；动态 SQL 让一条 SQL 按传参按需拼接。
2. **XML 映射文件**：`namespace`=接口全限定名；`<select id>`=方法名；`resultType`=行映射类；需在 `application.yml` 配 `mybatis.mapper-locations: classpath:mapper/*.xml`。
3. **四标签**：
   - `<if test="...">`：OGNL 表达式，真才拼。
   - `<where>`：自动剔除首个被拼条件前的 AND/OR；全空则不生成 WHERE（查全部）。
   - `<foreach collection/item/open/close/separator>`：遍历集合拼 `IN (1,2,3)`，配 `#{}` 预编译防注入。
   - `<set>`：动态 UPDATE，只拼传了的字段，自动去尾逗号，避免误置 NULL。

## 可视化
- show_widget 画"动态SQL按需拼接流程"图：入参(name="张",ageMin=null) → <where> → <if name>真拼AND → <if ageMin>假跳过 → <where>去首AND → 最终 SQL；并标注"全空→查全部"。直观建立动态拼接直觉。

## 用户接受度
- 用户先前表态"拦截器/PageHelper 晦涩"，本课选动态 SQL（标签式、直观）契合其接受度，讲解过程无障碍。

## 状态
- 讲解完成，课件已 present 供自测。待定：① 是否补 `reference/mybatis-dynamic-sql.md` 速查；② 是否把动态 SQL 实装进 `_01`（建 XML、改写 findByNameLike/deleteByIds）。等用户反馈。
