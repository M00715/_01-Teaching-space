# 0035 · 第6课收尾：分页实装 + PageResult bug + PageHelper 概念

## 分页实装落地（手写版，用户自己写的）
- Service `UserService.findPage(page,size)`：`offset=(page-1)*size` → `userMapper.findByPage(size,offset)` 拿当页 → `userMapper.CountAll()` 拿总数 → `new PageResult<>(list,total,page,size)`。
- Mapper 两条 SQL：`select * from user limit #{size} offset #{offset}` + `select count(*) from user`。
- 关键认知：分页一定是「两条 SQL」——LIMIT/OFFSET 只取当页，永远不给总数，所以必须单独 count(*)。

## 关键 bug：PageResult 构造函数是空的
- 用户写的 `public PageResult(List<T> list, long total, int page, int size) {}` —— 接收了参数但一个都没赋给字段。
- 后果：造出来的对象 list=null/total=0/page=0/size=0，这正是用户觉得「字段很乱、预想不到结果」的真正原因（不是不会预测，是结果本身错了）。
- 修复：补 `this.list=list; this.total=total; this.page=page; this.size=size;`；`@Data` 只生成 getter/setter，不会替手写构造函数填参。mvn -q compile 通过(EXIT=0)。

## 用数学推导讲透分页（N=50, s=10）
- 总页数 = ⌈N/s⌉ = ⌈50/10⌉ = 5 页（除不尽向上取整）。
- 第 p 页行范围：第 (p-1)·s+1 到 p·s 行。
- OFFSET = (p-1)·s（跳过前面所有整页）；LIMIT = s。
- 验证：p=2 → OFFSET=10 → 第 11~20 行；p=5 → OFFSET=40 → 第 41~50 行。

## 灌 50 条测试数据验证
- 用 python 脚本把 user 表补到 50 条（name 非空、其余可空），脚本执行后已删除。
- 直接按公式从库取：第2页 OFFSET 10 LIMIT 10 → id [11..20]；末页首条 id=41。与数学推导完全吻合。
- 注：application.yaml 的 spring.sql.init.mode=always 只跑 CREATE TABLE IF NOT EXISTS，不清数据，50 条会保留。

## PageHelper 拦截器（仅概念讲解，未实装）
- 本质：MyBatis 拦截器，一行 `PageHelper.startPage(page,size)` 自动拼 LIMIT/OFFSET + 自动发 count 查总数。
- 用法：加 pagehelper-spring-boot-starter 依赖 → Mapper 去掉手写 LIMIT → Service 用 startPage + PageInfo 取代手写 offset/count。
- 4 个坑：① startPage 必须紧贴要分页的查询（ThreadLocal 一次性消费，中间插别的查询会吃掉上下文）；② SQL 别再写 LIMIT（否则重复拼）；③ 返回的 list 实为 Page 子类，PageInfo 靠它取 total；④ ThreadLocal 按线程隔离，并发不串。
- 手写 vs 黑盒：学习阶段手写透明可控（已懂原理），真实项目多接口分页用 PageHelper 省事；二者二选一，不要同时用。

## 第6课状态
- 课件 0006-input-validation-pagination.html + 速查 input-validation-pagination.md 已就绪。
- 校验 + 分页两大块概念 + 实装均已覆盖（校验链路：DTO+@Valid+全局异常处理已在前序追问中落地并编译通过；分页手写版已落地并修复 bug、数据验证通过）。
- **第6课完整收尾**。用户计划当晚继续第7课。
