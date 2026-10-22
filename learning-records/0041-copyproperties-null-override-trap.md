# 0041 · BeanUtils.copyProperties 会把已设的值覆盖成 null（DTO→Entity 转换陷阱）

## 现象
在 Controller 把 DTO 转成 Entity 时：
```java
User u = new User();
u.setCreatedAt(LocalDateTime.now());        // 先设服务端生成的时间
BeanUtils.copyProperties(req, u);          // 再把客户端 DTO 拷过来
```
结果入库后 `created_at` 仍是 null，服务端赋值"白做"。

## 根因
`org.springframework.beans.BeanUtils.copyProperties(source, target)` 对每一个同名属性，**无条件**用 source 的值覆盖 target——包括 source 为 null 的情况。它没有"跳过 null"的默认行为。
`UserCreateRequest.createdAt` 因标注 `@Null`（语义上客户端不应传），值恒为 null，于是把刚 set 好的时间覆盖回 null。

## 正确写法
**「客户端字段先拷，服务端字段后填」**：
```java
User u = new User();
BeanUtils.copyProperties(req, u);          // 先拷客户端字段
u.setCreatedAt(LocalDateTime.now());       // 再覆盖服务端生成字段
```
凡是服务端生成的字段（createdAt、默认状态、内部标记等），都必须在 copyProperties **之后**赋值，否则被 null 覆盖。

## 延伸
- 若想"source 为 null 时跳过不拷"，需自定义（如 Apache Commons BeanUtils 的 `copyProperties` 仍不过滤 null；可写 `BeanWrapper` 遍历或引入 MapStruct/null-safe 拷贝工具）。Spring 原生无此能力。
- 反之，如果某些字段希望"以客户端为准、服务端不覆盖"，则要确保服务端不在 copy 之后再 set。顺序问题本质是"谁后写谁赢"。

## 关联
- 触发会话：learn_02 双闸门 A，第 ⑤ 项 createdAt 服务端赋值，用户初版顺序写反导致 bug，AI review 指出后修正。
- 教学定位：双闸门 A 的实战踩坑，属"看似会写、细节翻车"的典型，印证用户此前担忧"脱离 AI 写不出"——理解 copyProperties 语义即可独立避免。
