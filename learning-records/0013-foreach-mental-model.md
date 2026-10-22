# 0013 · &lt;foreach&gt; 心智模型澄清（第3课 task②）

日期：2026-10-16
状态：已澄清

## 学习者的混乱点
看不懂 `<foreach>` 各参数（collection/item/open/close/separator）的逻辑，大脑混乱。

## 顿悟点（21:57 确认已懂）
最卡的是 `open="("` / `close=")"` 这两个参数——一直不知道它们干嘛用。
直到把 foreach 的产物 `IN (1, 2, 3)` 摆出来，才恍然大悟：**open/close 就是在最终 SQL 里、列表前后包的那对括号**。
→ `open` = 循环【前】先写 `(`；`close` = 循环【后】再写 `)`；中间的 `1, 2, 3` 才是 `<foreach>` 遍历 List 生成的。
一句话锚定：`open/close` 管"括号"，`separator` 管"逗号"，`collection/item/#{item}` 管"括号里那串值"。

## 关键心智模型：`<foreach>` = 在 XML 里对 List 跑一个 for 循环

先理解目标 SQL：`DELETE FROM user WHERE id IN (1, 2, 3)` 意思是
"删掉 id 在集合 {1,2,3} 里的行"。foreach 的任务就是把 Java 的 `List<Long> ids=[1,2,3]`
变成括号+逗号那段 `(1, 2, 3)`。

等价 Java 伪代码：
```
StringBuilder sb = new StringBuilder();
sb.append("(");                 // open
for (Long id : ids) {          // collection="ids"；每轮元素叫 id（item="id"）
    sb.append(id);             // #{id} = 当前元素的值（预编译占位符，防注入）
    // 如果不是最后一个，前面补一个逗号
}
sb.append(")");                 // close
// 结果: "(1, 2, 3)"
```
（separator="," 是 MyBatis 替我们处理的"元素之间加逗号"，不用自己判断是不是最后一个）

## 五个属性逐一对应
- `collection="ids"` → 要遍历哪个集合（= 接口方法 `@Param("ids")` 的名字）
- `item="id"`        → 循环变量名，下面 `#{id}` 引用的就是它
- `open="("`         → 循环【之前】先输出的内容
- `close=")"`        → 循环【之后】输出的内容
- `separator=","`    → 每两个元素【之间】插入的内容

## 验证方式
`mvn test -Dtest=UserCrudTest` 已通过；手动 `DELETE /users/batch` body `[1,2,3]`。
