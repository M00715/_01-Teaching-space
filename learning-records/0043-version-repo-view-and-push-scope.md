# 0043 · 版本库查看/管理与 push 上传范围（机制澄清）

**日期**：2026-10-22
**类型**：B 类机制澄清（用户主动提问）

## 用户疑问

1. 版本库（Repository）怎么查看和管理？
2. `git push` 上传的是"最后一次 git commit 的东西"吗？

## 澄清要点

- **版本库位置**：项目内隐藏目录 `.git/`，不应手动编辑；用命令查看/管理。
  - 查看：`git log --oneline` / `git log --graph --all` / `git show <hash>` / `git diff` / `git diff --staged`
  - 回看旧版：`git checkout <hash>`（看完 `git checkout main` 跳回）
  - 管理：`git reset`（移指针，危险）、`git reflog`（后悔药）
- **push 范围**：不是"仅最后一次 commit"，而是**当前分支上远端缺失的整条提交链**（最新节点 + 所有缺失祖先）。
  - 链式结构决定：推最新必连带祖先。
  - 只推当前分支；未 commit 的改动不上传。

## 教学价值

用户对"版本库"和"push 边界"的心智模型偏模糊（以为 push 只传最后一条、版本库是个黑箱）。这两条是 Git 工程素养的基础，已补进 `reference/git-basics.md` §七、§八。属 B 类（懂机制），讲清后不易再混。
