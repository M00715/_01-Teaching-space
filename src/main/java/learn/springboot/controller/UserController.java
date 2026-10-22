package learn.springboot.controller;

import jakarta.validation.Valid;
import learn.springboot.common.PageResult;
import learn.springboot.common.Result;
import learn.springboot.dto.TransferRequest;
import learn.springboot.dto.UserCreateRequest;
import learn.springboot.entity.User;
import learn.springboot.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 控制层（Controller / 门面）。
 *
 * 它同样用构造器注入拿到 UserService——记住第 1 课的结论：
 * Controller 注入的是 Service，绝不是 Mapper。
 *
 * 本层的职责只有三个：接住 HTTP 请求、把参数绑定成对象、
 * 把 Service 结果转成 JSON 返回。业务和 SQL 都不在这里。
 *
 * 参数从哪来，由【参数上的注解】决定（与"增删改查"无必然绑定）：
 *   @RequestBody  → 来自请求体：客户端发送的 JSON 对象/数组（POST/PUT 传完整数据、DELETE 批量传 id 列表）转换成Java对象
 *   @RequestParam → 来自查询串：URL 中的 ?x=值（如 /users/search?name=张）
 *   @PathVariable → 来自路径段：URL 中的 /users/{id}（如 /users/1）
 * 注意：只有"客户端要发来一段 JSON 数据"时才用 @RequestBody；
 *       查询(GET)和按 id 删除通常不需要 body，改用 @RequestParam / @PathVariable。
 */

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** POST /users  —— 新增（客户端发送 User 的 JSON，故用 @RequestBody 接收） */
    @PostMapping
    public Result<User> create(@Valid @RequestBody UserCreateRequest req) {
        User u = new User();
        // copyProperties 只是把DTO的值搬进实体
        org.springframework.beans.BeanUtils.copyProperties(req, u);
        return Result.success(userService.create(u));
    }

    /** GET /users/{id}  —— 按 id 查（id 来自路径，不需要请求体） */
    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id) {
        return Result.success(userService.findById(id));
    }

    /** GET /users  —— 查全部(分页功能) */
    @GetMapping
    public Result<PageResult<User>> findPage(@RequestParam(defaultValue = "1")
                                                int page, @RequestParam(defaultValue = "10") int size) {

        return Result.success(userService.findPage(page, size));
    }

    /** GET /users/search?name=张 —— 按名字模糊查（独立路径，避免与 /{id} 冲突）
     *  请求示例：/users/search?name=张     ← 用查询参数（@RequestParam）
     *  注意：不能写成 "/search/"（尾斜杠在新版 Spring 不再被宽容匹配）。 */
    @GetMapping("/search")
    public Result<List<User>> findByCondition(
            // 关键：required=false 让 name/age 都变成「可选」。
            // 不传就收到 null，XML 里的 <if> 判断后自动跳过，实现真正的动态查询。
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "age", required = false) Integer age) {
        return Result.success(userService.findByCondition(name, age));
    }


    /** PUT /users/{id}  —— 修改（id 来自路径，User 完整对象来自请求体 JSON，故用 @RequestBody） */
    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id,
                               @Valid @RequestBody User user) {
        User u = new User();
        u.setId(id);
        org.springframework.beans.BeanUtils.copyProperties(user, u);
        return Result.success(userService.update(u));
    }

    @PostMapping("/transfer")
    public Result<String> transfer(@RequestBody TransferRequest req) {
        userService.transfer(req.getFromName(), req.getToName(), req.getAmount());
        return Result.success("转账成功");
    }

    /** DELETE /users/{id}  —— 单条删除（id 来自路径，不需要请求体，故【无】@RequestBody） */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功");
    }

    /** DELETE /users/batch  —— 批量删除（客户端把 id 数组 [1,2,3] 放在请求体 JSON 里，故用 @RequestBody 接收） */
    @DeleteMapping("/batch")
    public Result<Integer> deleteByIds(@RequestBody List<Long> ids) {
        return Result.success(userService.deleteByIds(ids));
    }
}
