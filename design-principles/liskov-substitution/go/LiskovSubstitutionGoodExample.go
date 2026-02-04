/**
 * 里氏代换原则
 * 所有引用基类的地方必须能透明地使用其子类的对象，简单说即一个地方使用了父类，那么也可以使用子类。
 */
package main

import (
  "fmt"
)

type Shape interface {
  draw()
  area() float64
}

type square struct {
  side float64
}

// 不重写父类的draw方法
// 另外起名，或通过重载得到新方法
func (s square) drawSquare() {
  if s.checkArea() {
    fmt.Println("Haha Drawing Square. area:", s.area())
  } else {
    fmt.Println("Don't draw square")
  }
}

func (s square) checkArea() bool {
  if s.area() > 100 {
    return false
  }
  return true
}

func (s square) area() float64 {
  return s.side * s.side
}

func (s square) draw() {
  fmt.Println("Drawing Shape. area:", s.area())
}

type rectangle struct {
  width  float64
  height float64
}

func (r rectangle) area() float64 {
  return r.width * r.height
}

func (r rectangle) draw() {
  fmt.Println("Drawing Shape. area:", r.area())
}

func main() {
  var square3 Shape = square{side: 6}
  var square4 Shape = square{side: 12}
  var rectangle3 Shape = rectangle{width: 8, height: 5}
  var rectangle4 Shape = rectangle{width: 9, height: 6}

  square3.draw()
  (square3.(square)).drawSquare()
  square4.draw()
  rectangle3.draw()
  rectangle4.draw()
}

/**
jarry@jarrys-MBP go % go run LiskovSubstitutionGoodExample.go 
Drawing Shape. area: 36
Haha Drawing Square. area: 36
Drawing Shape. area: 144
Drawing Shape. area: 40
Drawing Shape. area: 54
*/
