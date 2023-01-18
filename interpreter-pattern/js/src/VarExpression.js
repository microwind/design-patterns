import { Expression } from './Expression.js'

// 构建变量表达式，或者叫终端表达式，其他表达式求值时通过层层追溯最后指向这里
// 变量与执行环境的Key对应，最终会通过key获取传入的值
export class VarExpression extends Expression {
  constructor(key) {
    super()
    this.key = key
  }

  // 覆盖表达式，根据key获取变量
  interpret(context) {
    return context.get(this.key)
  }
}
