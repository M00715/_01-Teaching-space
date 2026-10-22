package learn.springboot;

import learn.springboot.entity.User;
import learn.springboot.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 第一个 @SpringBootTest：启动整个 Spring 容器，把真实的 UserMapper 注入进来，
 * 连真实数据库跑一遍「插入 → 查询 → 断言」，验证 CRUD 真的对。
 *
 * @Transactional：测试结束时自动回滚，避免一条条测试数据污染共享的 SQLite 文件。
 */
@SpringBootTest
@Transactional
class UserCrudTest {

    @Autowired   // 直接注入 Mapper（也可以注入 UserService，这里为了贴近 SQL 用 Mapper）
    private UserMapper userMapper;

    @Test
    void insertThenSelect() {
        // 1) 准备一条数据（不用 setId，主键由数据库自增生成）
        User u = new User();
        u.setName("测试用户");
        u.setAge(25);
        u.setEmail("test@example.com");

        // 2) 插入；insert 执行后 MyBatis 把自增主键回填到 u.getId()
        userMapper.insert(u);
        assertNotNull(u.getId(), "插入后主键应被回填");

        // 3) 再按 id 查出来
        User found = userMapper.findById(u.getId());
        assertNotNull(found, "应能按 id 查到刚插入的用户");

        // 4) 断言 name 相等（这就是任务要求的核心断言）
        assertEquals("测试用户", found.getName());
    }
}
