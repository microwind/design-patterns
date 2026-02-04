/**
 * 组合复用原则 - 反例
 * 这个例子违反了组合复用原则，因为采用了继承而不是组合。
 * 1. 人的身份会有多重角色，不适合用继承表达；继承更适合物种层级。
 * 2. 采用继承不够灵活，修改很麻烦，例如一个人既是经理也是雇员。
 * 对比：正例通过组合/聚合复用角色，让对象关系更灵活可扩展。
 */
package main

import "fmt"

type Person struct {
  name string
  age  int
}

func (p Person) getName() string {
  return p.name
}

func (p Person) getAge() int {
  return p.age
}

// 违规点：用结构体嵌入模拟继承，角色组合不灵活
type Employee struct {
  Person
  id    int
  title string
}

func (e Employee) work() bool {
  return true
}

// 角色变化会迫使类型层级不断扩展
type Engineer struct {
  Employee
}

func (e Engineer) work() bool {
  fmt.Printf("Engineer is working. id = %d, title = %s name = %s, age = %d\n", e.id, e.title, e.getName(), e.getAge())
  return true
}

type Manager struct {
  Employee
}

func (m Manager) work() bool {
  fmt.Printf("Manager is working. id = %d, title = %s name = %s, age = %d\n", m.id, m.title, m.getName(), m.getAge())
  return true
}

func main() {
  engineer := Engineer{Employee{Person{"Tom", 25}, 1001, "senior engineer"}}
  manager := Manager{Employee{Person{"Jerry", 45}, 2002, "advanced director"}}
  engineer.work()
  manager.work()
}

/**
jarry@jarrys-MBP go % go run CompositeReuseBadExample.go 
Engineer is working. id = 1001, title = senior engineer name = Tom, age = 25
Manager is working. id = 2002, title = advanced director name = Jerry, age = 45
*/
