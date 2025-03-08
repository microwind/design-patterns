import { ObserverAPI } from './ObserverAPI.js'

// 具体的观察者实现类，也可以看成订阅者，关联对应的主题类。
// 不同的观察者也可以对应多个主题
export class ConcreteObserver extends ObserverAPI {
  // 给观察者绑定主题，同时把观察者添加到主题列表
  constructor(subject, name) {
    super(name)
    this.subject = subject
    subject.register(this)
  }

  // 观察者更新事件，主题类有新发布时会批量调用，而无需逐个通知
  update(content) {
    console.log(
      `${
        this.constructor.name
      }::update() [subject.name = ${this.subject.name} content = ${content}]`
    )
  }
}
