# POM 修复与根因分析

> 项目：`D:\Workspace\_01`（Spring Boot 4.0.8 + Java 17，学习用 CRUD 工程）
> 现象：IntelliJ IDEA 报「找不到 springframework」

---

## 一、真正的根本原因（两层，均已修复）

### 根因 A：IDEA 从未成功导入 Maven 依赖（环境 / 导入问题，主因）

- 项目 `.idea/libraries` 目录**不存在** → IDEA 的 External Libraries 为空 → `org.springframework.*` 不在模块 classpath → 整片 Spring 类标红，被概括为「找不到 springframework」。
- 直接诱因：本机 `~/.m2/settings.xml` 配了**阿里云镜像**（`mirrorOf=*`）。Maven 命令行（`./mvnw`）走该镜像能正常下载；而 IDEA 导入工程时若没指向同一份 `settings.xml`（或首次导入时仓库不可达），依赖就下载失败并被缓存，之后不再重试。
- 证据：
  - `./mvnw dependency:resolve` → **exit 0**，所有依赖可解析；
  - `spring-boot-starter-webmvc`、`mybatis-spring-boot-starter:4.0.1`、`spring-boot-autoconfigure:4.0.8` 等 jar **都已存在于本地仓库**；
  - `.idea/libraries` 缺失，说明 IDEA 这侧根本没有把库链接进工程。

> 结论：pom 的依赖坐标本身**有效**，IDEA 报错主要是「没导入 + 镜像不匹配」造成的 classpath 缺失。

### 根因 B：代码引用了 Spring Boot 4 已移除的类（真实编译错误）

- `Application.java` 原有一行：
  ```java
  @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
  ```
  但存在两个问题：
  1. 没有 `import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;`；
  2. **更关键**：Spring Boot 4.0 已把 `DataSourceAutoConfiguration` 从核心 `spring-boot-autoconfigure` 中**移除/模块化**（实测 `spring-boot-autoconfigure-4.0.8.jar` 内已无任何 `datasource` 相关类，该类现由 `spring-boot-starter-jdbc` 对应的自动配置模块提供）。
- 命令行 `./mvnw compile`（修复前）报错：
  ```
  Application.java:[6,35] 找不到符号 类 DataSourceAutoConfiguration
  ```
  注意 `SpringApplication` / `SpringBootApplication` **并不报错** —— 说明 Spring 类本身齐全，问题只在这一处引用。

---

## 二、已做的修复

### 1. `pom.xml`（按你的要求重构）

原则：**只有 mybatis / sqlite / springframework，CRUD 可用，无 web 模板，不用 MyBatis Plus**。

| 变更 | 说明 |
|---|---|
| 删除 `spring-boot-starter-webmvc` / `spring-boot-starter-webmvc-test` | 你不需要 web 层 |
| 核心改为 `spring-boot-starter` | 即 springframework 核心 IoC / DI 容器 |
| 显式新增 `spring-boot-starter-jdbc` | springframework JDBC + **DataSource 自动配置**（SB4 必须显式引入，否则 `spring.datasource.*` 不会建 DataSource） |
| 保留 `mybatis-spring-boot-starter:4.0.1` | MyBatis（**非** MyBatis Plus） |
| 保留 `sqlite-jdbc`（runtime） | SQLite 驱动 |
| 测试改为 `spring-boot-starter-test` | 含 `@SpringBootTest` / JUnit5，替代原 webmvc-test / mybatis-test |

### 2. `Application.java`

- 删除 `exclude = {DataSourceAutoConfiguration.class}`（SB4 该类已不存在，且现在本就需要它来建 DataSource）；
- 加 `@MapperScan("learn.springboot.mapper")`。
  > 依据 ima 知识库《SpringBoot自动装配.md》：不加扫描，Mapper 接口找不到实现类，`@Autowired` 注入失败。

### 3. `application.yaml`

补齐 SQLite 数据源 + 启动建表：

```yaml
spring:
  application:
    name: _01
  datasource:
    url: jdbc:sqlite:./_01.db
    driver-class-name: org.sqlite.JDBC
    username: sa
    password: ""
  sql:
    init:
      mode: always
```

### 4. 新增 CRUD 冒烟样例（可随时删除）

- `src/main/resources/schema.sql`：建 `user` 表
- `src/main/java/learn/springboot/entity/User.java`
- `src/main/java/learn/springboot/mapper/UserMapper.java`（注解式 CRUD + `@Mapper`）
- `src/main/java/learn/springboot/CrudSmokeRunner.java`：启动后自动跑一遍 增/查/改/删

---

## 三、验证结果（真实运行）

`./mvnw spring-boot:run`：

```
HikariPool-1 - Added connection org.sqlite.jdbc4.JDBC4Connection@...
Started Application in 1.222 seconds
[CRUD] INSERT 成功，生成 id = 1
[CRUD] SELECT 成功，name = alice，email = alice@example.com
[CRUD] UPDATE 成功
[CRUD] DELETE 成功 —— 全流程 CRUD 跑通 ✅
```

`./mvnw test`（`@SpringBootTest` 上下文加载 + CRUD）：**exit 0 通过**。

---

## 四、在 IDEA 中彻底消除红字（关键收尾步骤）

命令行能跑 ≠ IDEA 不红。请在本机 IDEA 里执行：

1. 右侧 **Maven** 面板 → 点击 **Reload All Maven Projects**（或刷新图标）。
2. 确认 IDEA 用同一份 Maven 与镜像：
   - `Settings → Build → Build Tools → Maven`
   - **User settings file** 勾选并指向 `~/.m2/settings.xml`（这样才会走阿里云镜像）；
   - **Maven home path** 选与命令行一致（建议 Use wrapper 或指向同一 Maven）。
3. 若仍报 `cannot find declaration`：`File → Invalidate Caches / Restart → Invalidate and Restart`。

完成后「找不到 springframework」应彻底消失。

---

## 五、一句话总结

pom 依赖坐标本身有效；IDEA 报错的**主因**是「未导入依赖（镜像不匹配）」+「代码引用了 SB4 已移除的 `DataSourceAutoConfiguration`」。两处都已修复，并已用真实运行证明 CRUD 跑通。
