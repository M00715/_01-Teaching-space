# 0012 · XML Mapper + 第一个 @SpringBootTest（第 3 课动手任务②③）

日期：2026-10-16
状态：已完成并验证通过

## 学习者卡点
- 完全不会在 XML 里写 SQL，以前学 mapper+XML（SpringMVC）时很模糊。
- 从没写过 @SpringBootTest，不知道测试脚本长什么样、怎么跑。

## 已完成的动作（在 _01 中）
1. 新建 `src/main/resources/mapper/UserMapper.xml`：
   - namespace = learn.springboot.mapper.UserMapper（与接口绑定）
   - `<delete id="deleteByIds">` + `<foreach>` 展开成 `IN (1,2,3)`
2. `application.yaml` 增加 `mybatis.mapper-locations: classpath:mapper/*.xml`（必配，否则 Invalid bound statement）
3. `UserMapper.java` 增加 `int deleteByIds(@Param("ids") List<Long> ids);`（无注解，SQL 在 XML）
4. `UserService.java` 增加 `deleteByIds` 转发
5. `UserController.java` 增加 `DELETE /users/batch`（body 传 id 数组）
6. 新建 `src/test/java/learn/springboot/UserCrudTest.java`：@SpringBootTest + @Transactional，
   insert → findById → assertEquals(name)。@Transactional 让测试结束自动回滚，不污染共享库。

## 关键认知（已植入）
- XML Mapper 的绑定靠 namespace(=接口全名) + id(=方法名)。
- 一个方法 SQL 二选一：注解 或 XML，不能重复。
- 批量用 `<foreach>`：collection=@Param名, item=别名, open/close/separator 控制括号与逗号。
- 测试验证 CRUD 正确性比手动 curl 更稳（可重复、可断言）。

## 遗留/下一步
- 跑 `mvn test -Dtest=UserCrudTest` 验证（首次需联网下载 test 依赖）。
- 之后进第 4 课：数据源 / 实体 / 事务（@Transactional 在 Service 层的意义）。
