# 0022 · 转账功能 code review 三处问题（用户自查）

## 用户实现
用户自行补全转账功能：Mapper `reduceMoney/addMoney` 用 XML 实现（namespace/参数名均正确），
新增 `dto/TransferRequest`(fromName/toName/amount)、`Service.transfer`(@Transactional)、
`Controller` 端点。方向正确，但落码出现 3 个错。

## 问题
1. **🔴 死 bug · 条件写反**（`UserService.transfer` 第 52 行）：
   `if (amount != null && amount > 0) throw ...` —— 合法金额(>0)反而被拒；
   非法金额(≤0/null)放行。amount=-50 会使 `money = money - (-50)` → 张三变多钱、里斯变少钱（凭空造钱）。
   正确：`if (amount == null || amount <= 0) throw IllegalArgumentException("金额必须 > 0")`。
2. **🟠 中等 · 字符串拼接写错**（第 55 行）：
   `throw new RuntimeException("...余额不足：\" + fromName")` —— `\"` 是字面引号，
   `fromName` 没真正拼接，异常信息变成死字符串 `"...：" + fromName`。
   正确：`"转出用户不存在或余额不足：" + fromName`。
3. **🟡 规范 · HTTP 动词/命名**：转账是动作，用 `POST /users/transfer`（非 `PUT`）；方法名 `reset` 实为 transfer，已改名为 `transfer`。

## 修复 & 验证
- 已 Edit 修正上述 2 处逻辑 + 1 处规范；`mvn -q compile` 通过（exit 0）。
- 回滚观察法：`POST /users/transfer` 发 `{"fromName":"张三","toName":"王五","amount":50}`
  → 第一步张三扣成功、第二步王五不存在(0行)→抛异常→@Transactional 回滚→张三余额不变。

## 教学点
- 金额校验条件"非 null 且 >0 才合法"要写成 `==null || <=0 才抛`，写反是高频笔误。
- Java 里 `\"` 在字符串内是字面双引号，不是字符串结束；拼接变量要确认引号确实闭合在变量外。
