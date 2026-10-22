# MyBatis XML Mapper 写法速查（你以前学不懂的那块）

> 目标：搞清楚「接口 + XML」这套组合到底怎么绑起来、SQL 写哪、`<foreach>` 怎么展开。

## 一、为什么有 XML 写法？

`@Insert/@Select` 这种**注解写法**把 SQL 直接写在 Java 接口里，简单 CRUD 很方便。
但当 SQL 变复杂（动态条件 `if`、多表 `join`、批量 `foreach`）时，注解里拼字符串又臭又长，
于是 MyBatis 提供 **XML 写法**：把 SQL 挪到一个 `.xml` 文件里，和接口「分离但绑定」。

**关键点：无论注解还是 XML，都围着同一个 Mapper 接口转。**
接口是「契约」（定义有哪些方法），SQL 是「实现」（注解或 XML 二选一提供）。

## 二、绑定机制（最重要的心智模型）

```
UserMapper.java  (接口，定义方法 deleteByIds)
        │  靠什么绑？  →  XML 里的 namespace + 每个节点的 id
        ▼
UserMapper.xml   (namespace="learn.springboot.mapper.UserMapper")
        └─ <delete id="deleteByIds"> ... </delete>   ← id 必须 = 方法名
```

- `namespace` = 接口全限定名（包名+类名），错一个字母都绑不上。
- 节点 `id` = 接口里的方法名，必须一字不差。
- 一个方法**要么**用注解（接口上写 `@Delete`）、**要么**用 XML（xml 里写节点），
  不能两边都写，否则 MyBatis 报「重复语句」。

## 三、文件放哪 + 必须配置

XML 通常放 `src/main/resources/mapper/`，编译后进入 `classpath:mapper/`。
**光放文件不够，还要在 application.yaml 告诉 MyBatis 去哪找：**

```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true   # 列名 user_name → 字段 userName 自动映射
```

> 忘了配 `mapper-locations`，启动时不会报错，但调用该方法会抛
> `Invalid bound statement (not found)`——这是 XML 没被加载的典型症状。

## 四、`<foreach>` 批量写法

接口侧：
```java
int deleteByIds(@Param("ids") List<Long> ids);   // 没有 @Delete 注解
```
XML 侧：
```xml
<delete id="deleteByIds">
    DELETE FROM user WHERE id IN
    <foreach collection="ids" item="id" open="(" close=")" separator=",">
        #{id}
    </foreach>
</delete>
```
- `collection`：要遍历的参数名，对应 `@Param("ids")`。
- `item`：遍历时每个元素的别名，下面 `#{id}` 用的就是它。
- `open/close`：整体两侧加 `( )`；`separator`：元素间加 `,`。
- 结果：`IN (1, 2, 3)`。

## 五、`#{}` 还是 `${}`（安全红线）

- `#{id}` → 预编译参数（`?` 占位 + 值绑定），**防 SQL 注入，永远优先用这个**。
- `${id}` → 字符串拼接，会把值直接拼进 SQL，**有注入风险，只在动态表名/排序字段等极少数场景用**。

## 六、怎么验证写对了

1. 编译：`mvn -q compile`（XML 在 resources 下，编译会复制进去）。
2. 跑测试：`mvn test -Dtest=UserCrudTest`（用真实的 @SpringBootTest 验证）。
3. 或手动：启动后用 Postman `DELETE /users/batch`，body 选 raw/JSON 传 `[1,2,3]`。

> 常见坑：`Invalid bound statement (not found)` = namespace/id 拼错 或 mapper-locations 没配。
