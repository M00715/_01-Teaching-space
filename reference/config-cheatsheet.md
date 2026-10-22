# Spring Boot 配置速查卡（application.yaml）

> 对应 `_01/src/main/resources/application.yaml`。改完配置**必须重启**应用才生效
> （Spring Boot 默认不热加载，见第 2 课「改了代码不生效=没重启」卡片）。

## 一、改端口（你问的）
```yaml
server:
  port: 9090        # 默认 8080；改后请求发往新端口，如 localhost:9090/users
```
Properties 等价写法：`server.port=9090`

## 二、配置文件在哪、两种格式
- 位置：类路径根目录下的 `application.yaml`（推荐，层级清晰）或 `application.properties`。
- 两种格式等价，本项目用的是 yaml（缩进表示层级）。

## 三、除了端口，还能自定义什么（常用）
```yaml
spring:
  application:
    name: _01                       # 应用名（注册中心/日志里有用）
  datasource:                      # 数据源（当前是 SQLite）
    url: jdbc:sqlite:identifier.sqlite
    driver-class-name: org.sqlite.JDBC
    username: sa                   # 有账号的库（MySQL）才需要
    password: ""
    hikari:                        # 连接池调优（可选）
      maximum-pool-size: 10
  sql:
    init:
      mode: always                 # 启动时执行 schema.sql 建表
  jackson:
    default-property-inclusion: non_null   # JSON 忽略 null 字段（解决 age:null 显示）
    date-format: yyyy-MM-dd HH:mm:ss
  profiles:
    active: dev                    # 激活哪个环境配置

server:
  port: 9090
  servlet:
    context-path: /api            # 所有接口加统一前缀，如 /api/users
  address: 0.0.0.0                # 允许外部访问（默认只本机 127.0.0.1）

logging:
  level:
    learn.springboot: DEBUG        # 自己包的日志级别
    org.mybatis: INFO
  file:
    name: app.log                 # 日志写到文件（默认只打控制台）

mybatis:
  mapper-locations: classpath*:mapper/*.xml   # XML 版 Mapper 位置（第3课用）
  configuration:
    map-underscore-to-camel-case: true        # 下划线列名→驼峰字段（user_name→userName）
```

## 四、多环境配置（进阶）
建 `application-dev.yaml` / `application-prod.yaml`，用 `spring.profiles.active: dev` 切换；
`dev` 里可以端口用 9090、日志 DEBUG，`prod` 里端口 8080、日志 INFO。

## 五、一句话记优先级
「命令行参数 > 环境变量 > `application-{profile}.yaml` > `application.yaml`」。
同一项多处配置，越靠前越优先。
