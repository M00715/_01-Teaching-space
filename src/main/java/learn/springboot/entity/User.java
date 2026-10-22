package learn.springboot.entity;

import lombok.Data;

/**
 * 用户实体：对应数据库 user 表的一行。
 * Lombok 的 @Data 自动生成 getter/setter/toString/equals/hashCode，
 * MyBatis 正是通过 setter 把查询结果填回这个对象。
 */
@Data
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private Integer money;

}
