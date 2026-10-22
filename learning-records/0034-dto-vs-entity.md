# 0034 · DTO 是什么 / DTO 与 Entity 的区别

日期：2026-10-19

## 疑问
第 6 课课件里说"校验要标在入参专用 DTO 上"，用户问：**DTO 字段是啥？**

## 结论
DTO = **Data Transfer Object**，数据传输对象。一句话：**专门用来"接住一次请求数据、或组装一次响应数据"的普通 Java 类**，不对应数据库表。

项目里已经有一个现成 DTO：`dto/TransferRequest.java`
```java
@Data
public class TransferRequest {
    private String fromName;
    private String toName;
    private Integer amount;
}
```
前端发 `{"fromName":"张三","toName":"李四","amount":100}`，就用它接住。"DTO 字段"= 它里面的属性（fromName/toName/amount），名字与 JSON 的 key 对应，Jackson 按名映射。

## Entity vs DTO
| | Entity（实体） | DTO（数据传输对象） |
|---|---|---|
| 对应什么 | **数据库表的一行** | **一次请求/响应的形状** |
| 谁用 | MyBatis 映射结果 | Controller 接参 / 返回 |
| 字段 | = 表字段（User: id/name/age/email/money） | = 客户端实际该传的字段，可少于/多于表 |
| 校验注解 | **不该加**（查库回填时也会被校验，互相干扰） | **应该加**（只在入口校验） |
| 典型命名 | `User` | `UserCreateRequest` / `TransferRequest` / `XxxDTO` / `XxxVO` |

## 为什么校验必须写在 DTO 而不是 Entity
1. **时机不同**：校验只在"客户端传进来"时该发生；`findById` 查库回填 `User` 时不该触发校验（老数据可能不合新规则，一查就炸）。
2. **安全边界**：Entity 有 `id`、`money` 等字段，客户端不该能传。DTO 只声明"允许客户端传的字段"，天然挡掉越权赋值。这正是学习记录 0007 里"进阶：用 DTO 隔离输入与实体"说的做法——比"临时被设值再覆盖"更干净。
3. **契约稳定**：表结构变了（加列），不必强行改接口；接口要变，也不必动表。

## 与项目的衔接
- 第 6 课拟建 `UserCreateRequest`，就是 create 接口的入参 DTO：只含 name/age/email/money + 校验注解；`User` 实体保持原样用于落库。
- `TransferRequest` 已经证明了这个模式：user 表里没有 fromName/toName/amount 这三列（钱在 money 上），所以它**不能**是 User 实体，只能是 DTO。

## 一句话记忆
> **Entity 对着表，DTO 对着接口。** 表是"数据长什么样"，DTO 是"这次请求/响应长什么样"。
