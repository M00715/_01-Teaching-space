# Spring Boot CRUD 复习 · 资源清单

> 本文件是复习课程的唯一信任来源（theory of knowledge）。所有讲解从这里取料，不凭记忆杜撰。
> 一手个人资料来自 ima 知识库《Java后端课程资料》（id: `7516478123286370`），共 15 篇笔记。
> 当前教学工作区：`D:\Workspace\_01`（同时是待实战的 Spring Boot + MyBatis 项目）。

## Knowledge（知识来源）

### 一手：你的 ima 笔记（按需回看，按 CRUD 学习路径排序）

- [Spring之IoC与AOP.md](ima://kb/7516478123286370)
  Spring 容器与 IoC/DI、AOP。所有 Spring Boot 的地基——不懂它，autoconfig 只是黑盒。**Use for：讲分层、依赖注入、@Component/@Service/@Controller 注解时引用。**
- [SpringMVC.md](ima://kb/7516478123286370)
  一次请求如何进入 Controller、参数怎么接收、返回值怎么处理。**Use for：讲 REST 接口、@RequestMapping、JSON 返回时引用。**
- [MyBatis.md](ima://kb/7516478123286370)
  Mapper 接口 + SQL、表与对象映射、增删改查操作、缓存与动态 SQL。**Use for：讲数据访问层 CRUD 时引用，是本项目（_01）的实际 ORM。**
- [数据库MySQL与JDBC.md](ima://kb/7516478123286370)
  SQL 操作、JDBC 连接数据库、Web 三层架构。**Use for：讲表设计、建库建表、SQL 语句时引用。（注：_01 实际用 SQLite，SQL 方言差异极小，CRUD 逻辑通用。）**
- [SSM框架整合.md](ima://kb/7516478123286370)
  Spring 如何整合 Spring MVC + MyBatis，各层分工。**Use for：把前几篇串成整体时引用。**
- [SpringBoot自动装配.md](ima://kb/7516478123286370)
  为什么不用 XML、自动配置原理、Spring 与 Spring Boot 区别。**Use for：讲项目骨架、@SpringBootApplication、starter 依赖时引用。**
- [Spring IoC Bean配置的三种方式_notes.md](ima://kb/7516478123286370)
  XML / 注解 / 配置类三种装配 Bean 的方式对比（B站视频笔记）。**Use for：深入 IoC 时引用。**

### 一手：底层支撑（遗忘时按需补）

- [JavaWeb之Servlet与JSP.md](ima://kb/7516478123286370)
  Tomcat、Servlet、请求/响应链路。SpringMVC 的底层。**Use for：理解「一次请求怎么走到 Controller」时引用。**
- [反射Reflection.md](ima://kb/7516478123286370)
  Class 对象、动态获取属性/方法。Spring IoC 动态创建对象的机制。**Use for：讲 IoC 底层原理时引用。**
- [集合框架Collection.md](ima://kb/7516478123286370)
  List/Set/Map 体系。**Use for：日常编码、返回集合数据时引用。**
- [IO流与序列化.md](ima://kb/7516478123286370)
  File、字节流/字符流、对象序列化。**Use for：读写文件、对象持久化时引用。**
- [泛型枚举与常用工具类.md](ima://kb/7516478123286370)
  泛型、枚举、Math/Random/日期类。**Use for：写实体类、工具方法时引用。**
- [并发编程工具包JUC.md](ima://kb/7516478123286370)
  线程池、锁、原子类。**Use for：涉及并发/异步时引用（CRUD 基础阶段一般用不到）。**
- [网络编程TCP与UDP.md](ima://kb/7516478123286370)
  TCP/UDP 套接字。**Use for：理解 HTTP 底层时引用（CRUD 基础阶段一般用不到）。**
- [teaching-style-analysis.md](ima://kb/7516478123286370)
  对你这 14 篇笔记的教学风格整体分析。**Use for：开篇把握全貌时引用。**

### 外部高信任来源（用于交叉验证与权威定义）

- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/)
  权威参考。**Use for：注解语义、自动配置项、版本差异存疑时查阅。**
- [MyBatis 官方文档](https://mybatis.org/mybatis-3/)
  Mapper/XML 语法权威。**Use for：MyBatis 配置、动态 SQL 语法查漏。**
- [Baeldung — Spring 系列教程](https://www.baeldung.com/spring-boot)
  大量可运行的 CRUD 范例。**Use for：想看「标准写法长什么样」时参考。**

## Wisdom（社区 / 实践来源）

- 暂未记录社区偏好。
  CRUD 阶段建议：遇到报错优先 Stack Overflow / 中文 Stack Overflow（segmentfault、知乎），B站同系列视频可作补充讲解。若你不想加入社区，告诉我，后续不再提议。

## Gaps（尚缺的资料）

- 缺一份「最小可跑 CRUD 样例工程」对照：你的笔记偏概念，动手代码少。本项目 `_01` 本身即 Spring Boot + MyBatis 工程，可充当随课动手的载体。
- 缺「请求从浏览器到数据库」的完整时序图：计划在第一课用图示补上。
