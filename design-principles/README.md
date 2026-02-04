# 设计模式的7大原则

<img src="../docs/oop-principles.png">

本仓库收录了 7 大面向对象设计原则的正例与反例，覆盖 Java、Go、C、JavaScript、TypeScript、Python 多语言实现，便于对比理解。

**原则目录**
- 开闭原则，对扩展开放，对修改关闭。[查看例子](./open-closed/)
- 单一职责原则，一个类只负责一件事或一类功能。[查看例子](./single-responsibility/)
- 依赖倒置原则，高层模块依赖抽象而非实现。[查看例子](./dependency-inversion/)
- 接口隔离原则，依赖最小接口，避免臃肿接口。[查看例子](./interface-segregation/)
- 合成复用原则，优先组合而非继承。[查看例子](./composite-reuse/)
- 迪米特法则（最少知道原则），减少对象间不必要依赖。[查看例子](./law-of-demeter/)
- 里氏替换原则，子类可替换父类且行为一致。[查看例子](./liskov-substitution/)

**目录约定**
- 每个原则目录下包含 `java/` `go/` `c/` `js/` `ts/` `python/`。
- 每种语言均拆分为 `GoodExample` 与 `BadExample`。

**示例命名**
- 例：开闭原则 Java 正例 `open-closed/java/src/OpenClosedGoodExample.java`，反例 `open-closed/java/src/OpenClosedBadExample.java`
- 例：开闭原则 Go 正例 `open-closed/go/OpenClosedGoodExample.go`，反例 `open-closed/go/OpenClosedBadExample.go`

**运行方式**
- Java：在具体原则目录执行 `javac -d out src/*.java test/*.java` 后运行 `java -cp out test.Test`
- Go：`go run go/<Principle>GoodExample.go` 或 `go run go/<Principle>BadExample.go`
- C：`gcc c/<principle>_good_example.c -o /tmp/example && /tmp/example`
- JavaScript：`node js/<Principle>GoodExample.js`
- TypeScript：`ts-node ts/<Principle>GoodExample.ts`
- Python：`python3 python/<principle>_good_example.py`

说明：各目录 README 中提供了更详细的原则解读与代码说明。
