/**
 * 依赖反转原则 - 反例
 * 这个例子违反了依赖反转原则，为了方便理解把相关类放在一起。
 * 1. 高层业务应用直接依赖了低层逻辑对象，过度耦合。
 * 2. 依赖的是实现对象而不是接口或抽象类，不便于扩展。
 * 对比：正例让高层依赖抽象接口，具体实现可替换、可扩展。
 */
package main

import "fmt"

type MessageSender struct {
  name string
}

type MailSender struct {
  name string
}

type PushSender struct {
  name string
}

func (s MessageSender) send(content string) {
  fmt.Printf("%s has sent: %s\n", s.name, content)
}

func (s MailSender) send(content string) {
  fmt.Printf("%s has sent: %s\n", s.name, content)
}

func (s PushSender) send(content string) {
  fmt.Printf("%s has sent: %s\n", s.name, content)
}

// 违规点：高层模块持有具体发送器
type Notification struct {
  messageSender MessageSender
  mailSender    MailSender
  pushSender    PushSender
}

// 直接组装具体实现，无法通过抽象替换
func NewNotification() Notification {
  return Notification{
    messageSender: MessageSender{name: "message-sender"},
    mailSender:    MailSender{name: "mail-sender"},
    pushSender:    PushSender{name: "push-sender"},
  }
}

// 高层方法直接依赖具体实现
func (n Notification) sendMessage(content string) {
  fmt.Println("send message by Notification.")
  n.messageSender.send(content)
}

func (n Notification) sendEmail(content string) {
  fmt.Println("send email by Notification.")
  n.mailSender.send(content)
}

func (n Notification) sendPush(content string) {
  fmt.Println("send push by Notification.")
  n.pushSender.send(content)
}

func main() {
  notification := NewNotification()
  content := "hello, how are you!"
  notification.sendMessage(content)
  notification.sendEmail(content)
  notification.sendPush(content)
}

/**
jarry@jarrys-MBP go % go run DependencyInversionBadExample.go 
send message by Notification.
message-sender has sent: hello, how are you!
send email by Notification.
mail-sender has sent: hello, how are you!
send push by Notification.
push-sender has sent: hello, how are you!
*/
