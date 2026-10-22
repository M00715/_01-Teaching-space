package learn.springboot.mapper;

import learn.springboot.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 数据访问层（Mapper 接口）。
 *
 * 注意：这里只有「接口 + 注解」，没有任何实现类！
 * 真正的 SQL 执行由 MyBatis 在运行时用「动态代理」生成实现类来完成。
 * 这个接口之所以能变成 Spring Bean，是因为 Application 上的
 * @MapperScan("learn.springboot.mapper") 扫描到了它。
 */
public interface UserMapper {

    /** 新增：useGeneratedKeys 让 SQLite 把自增主键回填到 user.id */
    @Insert("INSERT INTO user(name, age, email, money) VALUES(#{name}, #{age}, #{email}, #{money})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Select("select * from user limit #{size} offset #{offset} ")
    List<User> findByPage(@Param("size") int size, @Param("offset") int offset);

    @Select("select count(*) from user")
    Long CountAll();


    List<User> findByCondition(@Param("name")  String name, @Param("age") Integer age);

    void update(User user);

    @Delete("DELETE FROM user WHERE id=#{id}")
    void deleteById(Long id);

    /** 批量删除：XML 写法（见 resources/mapper/UserMapper.xml）。
     *  注意：这里只声明方法 + @Param，SQL 写在 XML 里，没有 @Delete 注解。 */
    int deleteByIds(@Param("ids") List<Long> ids);

    int reduceMoney(@Param("fromName") String fromName, @Param("amount") Integer amount);

    int addMoney(@Param("toName") String toName, @Param("amount") Integer amount);


}
