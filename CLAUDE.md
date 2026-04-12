# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

设计模式与编程思想知识库，包含 23 种经典设计模式、7 大 OOD 原则、多种编程范式、分层架构（MVC/MVP/MVVM）、DDD、微服务架构的讲解与多语言实现示例。作者：Jarry 李春平。

## 仓库结构

仓库按主题组织为顶层目录，每个设计模式目录下按语言分子目录：

- `{pattern-name}/` — 每个设计模式一个目录（如 `factory-pattern/`, `observer-pattern/`）
  - `{lang}/src/` — 源码（lang = `c`, `java`, `js`, `python`, `ts`, `go`）
  - `{lang}/test/` — 测试入口
  - `{lang}/README.md` — 该语言实现的代码结构与运行方式
  - `README.md` — 模式总览（原理、UML、多语言代码片段）
- `design-principles/` — 7 大 OOD 原则，每个原则一个子目录，含正例和反例
- `programming-paradigm/` — 编程范式（OOP、FP、AOP、EDP、RP 等），含子目录和对比文档
- `mvx/` — MVC / MVP / MVVM 架构示例
- `domain-driven-design/` — DDD 多语言实现（含 web 版本）
- `microservice-architecture/` — 微服务架构模式集合
- `practice-projects/` — 实践项目（springwind, knife, springboot4ddd, gin-ddd, gin-mvc, spring-ai）
- `docs/` — 图片资源、UML 图、文章

## 运行示例代码

每个模式的每种语言实现都是独立的，在对应的 `{pattern}/{lang}/` 目录下运行：

```shell
# Java
cd {pattern}/java && javac test/Test.java && java test/Test

# JavaScript (ESM)
cd {pattern}/js && node test/test.js
# 或: npm test

# Python
cd {pattern}/python && python test/test.py

# Go
cd {pattern}/go && go run test/test.go

# C
cd {pattern}/c && gcc test/*.c src/*.c && ./a.out

# TypeScript
cd {pattern}/ts && tsc && node test/test.js
```

## 内容约定

- 所有设计模式均提供 6 种语言实现：C、Java、JavaScript、Python、TypeScript、Go
- 每种语言实现遵循统一的 `src/` + `test/` 目录结构
- 模式顶层 README.md 使用中文撰写，包含原理说明、UML 图、实现步骤、多语言代码片段
- 设计原则目录同时包含正例代码和反例代码（`*_counter.java`）
- 仓库内容为中英双语（主体中文，部分有 `_en.md` 英文版本）
- JS 项目使用 ESM（`"type": "module"`），Go module 名统一为 `microwind`
