# 0024 · 泛型静态工厂方法的 <T> 拆解（Result.success）

## 用户困惑
看到 `public static <T> Result<T> success(T data)`，分不清三个 T 各指什么，以为"第一个 T 是返回类型"。

## 纠正
1. `public static <T>` 里的 `<T>` 是**类型参数声明**（不是返回类型），"声明本方法引入泛型变量 T"。
2. `Result<T>` 是**返回类型**：返回 Result，其 data 字段类型为 T。
3. `T data` 是**参数类型**：传入的 data 为 T。
4. 三处 T 是**同一个 T**，编译期一起绑定同一具体类型，由调用实参推断。
5. 为何 static 必须自己写 `<T>`：`Result<T>` 类上的 `<T>` 是实例级（new Result<User>() 时绑定），static 方法不依附实例、拿不到，故需方法级重新声明。
6. 对照实例方法 `public T getData()` 可直接用类的 T，无需重声明。
7. 推断示例：`Result.success(user)` → T=User → `Result<User> success(User)`；`Result.success("x")` → T=String → `Result<String>`。

## 教学要点
泛型方法签名 `<T> 返回类型 方法名(参数)`：声明位必在最前；返回值与参数里的 T 与之同名同义。
