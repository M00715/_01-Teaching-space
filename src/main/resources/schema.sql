-- 应用启动时由 Spring 自动执行（application.yaml 里 spring.sql.init.mode=always）
-- 建表：用户表，id 为 SQLite 自增主键
CREATE TABLE IF NOT EXISTS user (
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    name  TEXT NOT NULL,
    age INTEGER NOT NULL ,
    email TEXT
);
