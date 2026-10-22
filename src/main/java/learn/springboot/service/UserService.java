package learn.springboot.service;

import learn.springboot.common.BizCode;
import learn.springboot.common.BizException;
import learn.springboot.common.PageResult;
import learn.springboot.common.Result;
import learn.springboot.entity.User;
import learn.springboot.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 业务层（Service）。
 *
 * 这里用「构造器注入」拿 Mapper：依赖写在构造参数里，Spring 自动把
 * MyBatis 生成的 UserMapper 代理塞进来。比起字段上写 @Autowired，
 * 构造器注入的依赖是不可变的（final），也更方便写单元测试。
 *
 * Service 不碰 SQL，只组织业务逻辑。本例 CRUD 逻辑很简单，就是转发给 Mapper。
 */
@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User create(User user) {
        userMapper.insert(user);
        return user;            // insert 后 user.id 已被 MyBatis 回填
    }

    public User findById(Long id) {
        return userMapper.findById(id);
    }


    public List<User> findByCondition(String name, Integer age) {
        return userMapper.findByCondition(name, age);   // 返回列表，不要强转成单个 User
    }


    public PageResult<User> findPage(int page, int size) {
        int offset = (page - 1) * size;
        List<User> list = userMapper.findByPage(size, offset);
        Long total = userMapper.CountAll();
        return new PageResult<>(list, total, page, size);

    }

    public User update(User user) {
        userMapper.update(user);
        return user;
    }

    @Transactional
    public void transfer(String fromName, String toName, Integer amount) {
        if (amount == null || amount <= 0) throw new BizException(BizCode.PARAM_INVALID);

        int r1 = userMapper.reduceMoney(fromName, amount);
        if (r1 == 0) throw new BizException(BizCode.INSUFFICIENT_BALANCE);

        int r2 = userMapper.addMoney(toName, amount);
        if (r2 == 0) throw new BizException(BizCode.USER_NOT_FOUND);

    }

    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    public int deleteByIds(List<Long> ids) {
        return userMapper.deleteByIds(ids);
    }
}
