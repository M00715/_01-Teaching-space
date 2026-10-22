# 0044 · Git 分支是什么（机制澄清）

日期：2026-10-22
类型：B 类机制盲区澄清（用户追问"分支又是什么"）

## 用户疑问
学完三大区域 / 版本库 / push 范围后，追问"分支又是什么"。

## 讲清的核心
1. **分支本质**：指向某个 commit 的轻量可移动指针 / 书签。版本库里真正存的是 commit 链（A→B→C→D），分支名只是贴在新节点上的标签，每次 commit 标签自动前移。
2. **HEAD**：特殊指针，表示"当前站在哪个分支"，`On branch main` 即 HEAD→main。
3. **为什么需要分支**：并行开发不污染主线；真实团队一人一分支 / 一功能一分支，做完 `git merge` 合回 main。
4. **串起已学点**：`git branch` 列分支 / `-M main` 改名 / `switch -c` 新建切过去；`master` 是旧默认名、`main` 是 GitHub 现默认名（故 learn_02 `init` 出 master 我们手动改名）。
5. **与强推关系**：`push --force-with-lease origin main` 的 main 指"该分支"，故强推以分支为单位 → 再一次印证"没有强推单个文件"。

## 教学衔接
- 已补进 `reference/git-basics.md` 第九节「分支到底是什么」。
- 用户当前仍卡在 ② Git 阶段实操收尾：learn_02 的 `git push --force-with-lease origin main` 未真正执行成功（amend 后 non-fast-forward）。
