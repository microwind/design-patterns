/**
 * 依赖倒置。
 * 1. 高层次模块不依赖于低层的实现，而是依赖于低层的抽象。
 * 2. 接口和抽象类不应该依赖于具体实现类，而是具体实现类依赖接口或抽象类。
 */

package main

import "fmt"

/*
 消息通知业务应用类，依赖低层模块的接口，而不是实现
*/
type Notification struct {
  sender Sender
}

// 依赖抽象发送方法，根据类型执行某个具体对象的方法
func (n *Notification) send(content string) {
  fmt.Println("send content by Notification.")
  n.sender.send(content)
}

func (n *Notification) setSender(sender Sender) {
  n.sender = sender
}

/*
 发送者的接口或抽象类，供实际发送者对象实现
*/
type Sender interface {
  send(content string)
}

/*
 MessageSender实现了Sender接口
*/
type MessageSender struct {
  name string
}

func (m *MessageSender) send(content string) {
  // do Something
  fmt.Printf("%s has sent: %s\n", m.name, content)
}

/*
 MailSender实现了Sender接口
*/
type MailSender struct {
  name string
}

func (m *MailSender) send(content string) {
  // do Something
  fmt.Printf("%s has sent: %s\n", m.name, content)
}

/*
 PushSender实现了Sender接口
*/
type PushSender struct {
  name string
}

func (p *PushSender) send(content string) {
  // do Something
  fmt.Printf("%s has sent: %s\n", p.name, content)
}

// 测试验证
func main() {
  notification := &Notification{&MailSender{}}
  var content = "hello, how are you!"
  notification.send(content)
  // 更换为MessageSender
  notification.setSender(&MessageSender{})
  notification.send(content)
  // 更换为PushSender
  notification.setSender(&PushSender{})
  notification.send(content)
}

/**
jarry@jarrys-MBP go % go run DependencyInversionGoodExample.go 
send content by Notification.
 has sent: hello, how are you!
send content by Notification.
 has sent: hello, how are you!
send content by Notification.
 has sent: hello, how are you!
*/
