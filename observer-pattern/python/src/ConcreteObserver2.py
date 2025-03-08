# -*- coding: utf-8 -*-
"""
@author: jarry
"""

from src.ObserverAPI import ObserverAPI


# 具体的观察者实现类，也可以看成订阅者，关联对应的主题类。
# 不同的观察者可以对应不同的主题。
class ConcreteObserver2(ObserverAPI):
    # 这里没有在构造器就绑定某个主题，而是从客户角度去注册观察者
    # 观察者更新事件，主题类有新发布时会批量调用，而无需逐个通知

    # def update(self, content):
    #     print(self.__class__.__name__ + '::update() [content = ' + content +']')
    
    pass