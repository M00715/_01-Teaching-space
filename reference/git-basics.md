# Git 基础 · 参考速查（压缩精华）

> 配 `lessons/0009-git-basics.html` 使用。本课讲机制，本文是「合上课件也能查」的压缩版。
> 最后更新：2026-10-22

## 一、三大区域模型（本地）

```
工作区  --git add-->  暂存区  --git commit-->  版本库  --git push-->  远程仓库(GitHub)
```

- **工作区**：你编辑的文件。
- **暂存区**：`git add` 搬进来的"待提交清单"。
- **版本库**：`git commit` 生成的永久快照历史。
- **远程仓库**：GitHub 服务器，<code>git push</code> 同步。
- `.gitignore` 忽略的（`target/` / `*.sqlite` / `.idea/`）永远进不了暂存区。

## 二、提交三板斧（每次改完都用）

```bash
git status                     # 看改动 + 确认忽略生效
git add .                      # 暂存（单文件：git add 文件名）
git commit -m "描述改了啥"      # 追加一个新节点，不是覆盖
git push                       # 同步到 GitHub（也是追加）
```

**核心**：`commit` 是**追加快照**，历史只增不减，旧节点永不会因新提交而丢失。

## 三、分支 / 远程

| 命令 | 作用 |
|------|------|
| `git branch` | 看分支，当前分支前有 `*` |
| `git branch -M main` | 当前分支改名 main（对齐 GitHub 默认） |
| `git remote -v` | 看远程地址（少了/错了就 `not found`） |
| `git remote add origin URL` | 登记远程（首次） |
| `git remote set-url origin URL` | 改远程地址（**本地命令，不用去网站**） |
| `git push -u origin main` | 首次推送并记下上游，之后直接 `git push` |

## 四、强推陷阱（重点）

- `git commit --amend` = **改写紧挨着的上一次提交**（哈希变，旧节点消失）。
- **铁律**：已 push 的提交不要 amend → 否则本地/远端历史对不上 → 普通 `git push` 报 `non-fast-forward` 被拒。
- 解决（仅限你独自开发的安全场景）：

```bash
git push --force-with-lease origin main
```

- `--force-with-lease` 比裸 `--force` 稳：若远端被别人改过它会拒绝，不盲目覆盖。
- 正常改动**永远走新提交**，amend 只用于"刚提交完、还没 push、发现小错"。

## 五、单个文件操作（补）

- 只提交一个文件：`git add 文件名` + `git commit`（区别于 `git add .`）。
- 用上次提交覆盖本地文件（破坏性）：`git restore 文件名`（老写法 `git checkout -- 文件名`）。
- 用某个旧版本覆盖单文件：`git checkout <哈希> -- 文件名` 再提交。
- **没有"强推单个文件"**——push 最小单位是 commit（整条分支快照）。

## 六、learn_02 实操卡点解法（2026-10-21–22）

1. 之前 `git push` 报 `repository '.../learn-02-crud.git/' not found` → 仓库名写错，少了 `-demo-`。
2. 后来 `git commit --amend` 改了说明 → 本地历史改写。
3. 若 GitHub 上已有旧提交，普通 push 会 `non-fast-forward`。
4. 解法：先确认地址，再强推：

```powershell
git remote -v                                              # 确认是 learn-02-crud-demo.git（无尾杠）
git push --force-with-lease origin main                   # 强推覆盖
```

- 令牌（PAT）仍有效，不用重生成；`Exception.ToString()` 报错是无害的凭据管理器 bug，忽略。
- 登录框：用户名 `M00715`，密码填 **PAT**（不是 GitHub 登录密码）。

## 七、查看与管理本地版本库

版本库存于隐藏目录 `<项目>/.git/`，**不要手动编辑**，用命令查看/管理：

| 命令 | 作用 |
|------|------|
| `git log --oneline` | 提交历史列表（每个快照一行） |
| `git log --oneline --graph --all` | 带分支图看全貌 |
| `git show <哈希>` | 看某次提交具体改了哪些行 |
| `git diff` | 工作区 vs 上次 commit 的差异 |
| `git diff --staged` | 暂存区 vs 上次 commit 的差异 |
| `git checkout <哈希>` | HEAD 临时跳到旧提交"看一眼"（看完 `git checkout main` 跳回） |
| `git reset <哈希>` | 移动分支指针（回退，危险） |
| `git reflog` | 记录 HEAD 每次移动，误删历史可找回（后悔药） |

## 八、push 上传的是"最后一次 commit"吗？（易错）

**不是只有最后一次。** `git push` 上传的是**当前分支上、远端还缺失的所有提交**——即从"本地与远端最后一次重合点"到你最新 commit 的**整条缺失链**。

- 原因：commit 链式，每个指向上一个父 commit；推最新节点必须连带祖先，远端才能拼出完整历史。
- 例：本地 `A→B→C→D`、远端只有 `A` → push 补齐 `B、C、D`，不只是 `D`。
- 边界：`push` **只推当前分支**；**未 commit 的工作区/暂存区改动不上传**（必须先 `add`+`commit`）。

## 九、分支（branch）到底是什么

**核心：分支就是一个「指向某个 commit 的轻量指针 / 可移动书签」。**

- 版本库里真正存的是一串 commit 链条：`A → B → C → D`。
- 分支名（如 `main`）只是贴在链条某个节点上的标签，指向最新的那个 commit。
- 你每 `commit` 一次，当前分支这个标签就**自动往前移到新节点**：`A→B→C→D` 时 `main` 指向 `D`；提交 `E` 后变 `A→B→C→D→E`，`main` 移到 `E`。
- **HEAD**：一个特殊指针，表示"你当前站在哪个分支上"。`git status` 第一行 `On branch main` 就是 HEAD 指向 `main`。

**为什么要有分支**：让你在不污染主线的情况下并行做事。

```
main:        A → B → C → D
                  \
feature/login:     E → F      ← 新分支从 C 分叉，独立开发，不影响 main
```

做完后 `git merge feature/login` 把 `E、F` 合回 `main`。这就是真实团队"一人一分支 / 一功能一分支"的工作流。

**你学过的几个分支相关点串起来**：

| 你用过 / 见过的 | 含义 |
|----------------|------|
| `git branch` | 列出所有分支，`*` 标记当前（HEAD 所在） |
| `git branch -M main` | 把当前分支改名为 `main` |
| `git branch 新名` | 基于当前 commit 新建一个分支（不切换） |
| `git switch 新名` / `git checkout 新名` | 切到该分支（HEAD 移动过去） |
| `git switch -c 新名` | 新建并立即切过去（最常用） |
| `master` vs `main` | 同一回事的不同默认名；旧 Git 默认 `master`，GitHub 现默认 `main`，所以你 `init` 后是 `master`、我们手动 `-M main` 对齐 |
| `git push -u origin main` | 把本地的 `main` 分支推到 GitHub，并建立跟踪关系 |

**和强推的关系**：`git push --force-with-lease origin main` 的 `main` 指的就是"把**这个分支**的本地历史覆盖到远端同名分支"——所以强推是以**分支**为单位的，这也解释了我们之前说的"没有强推单个文件"。

> 一句话：**分支 = 贴在 commit 链上的可移动标签；HEAD = 你脚下的那个标签；切换分支就是让 HEAD 指向别处。**

### 🚩 常见误区：每次提交都要设分支？

**不用。** 分支是「一次设置、长期存在」的标签，不是每次提交要重选的开关。

- `git init` 后自动就有默认分支（`master`）；你 `git branch -M main` 改成 `main` 后，它就一直叫 `main`。
- 只要你**不主动切走**，`HEAD` 就一直停在 `main`；之后每次 `git commit` 都**自动落到当前分支（main）**，完全不用再指定分支。
- 只有想换条线干活时才切：`git switch 别的`。切过去后，新 commit 才落在新分支。
- 远程跟踪也是一次性的：`git push -u origin main` 首次把本地 main 和远端 main 绑好，之后直接 `git push` 就行，不用每次写 `origin main`。
