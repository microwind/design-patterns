#include "func.h"

void concrete_observer_set_subject(ConcreteObserver *observer, Subject *subject)
{
  observer->subject = subject;
}

//  观察者更新事件，主题类有新发布时会批量调用，而无需逐个通知
void concrete_observer_update(ConcreteObserver *observer, char *content)
{
  printf("\r\n ConcreteObserver::update() [subject->name = %s content = %s]", observer->subject->name, content);
}

// 给观察者绑定主题，同时把观察者添加到主题列表
ConcreteObserver *concrete_observer_constructor(char *name)
{
  printf("\r\n concrete_observer_constructor() [构建ConcreteObserver]");
  Observer *observer = (Observer *)malloc(sizeof(Observer));
  ConcreteObserver *concrete_observer = (ConcreteObserver *)observer;
  strncpy(observer->name, name, 50);
  concrete_observer->set_subject = &concrete_observer_set_subject;
  concrete_observer->update = &concrete_observer_update;

  return concrete_observer;
}