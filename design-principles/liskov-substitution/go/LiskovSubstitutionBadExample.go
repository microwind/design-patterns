/**
 * 里氏代换原则 - 反例
 * 这个例子违反了里氏代换原则，为了方便理解把相关类放在一起。
 * 1. 子类覆盖了父类的方法，改变了父类方法的意图。
 * 2. 因为子类改变了父类行为，如果用子类替换其父类可能会引起问题。
 * 对比：正例不覆盖父类的非抽象方法，通过新增方法扩展行为。
 */
package main

import "fmt"

type Shape interface {
  draw()
  area() float64
}

// 违规点：子类行为改变父类预期
type square struct {
  side float64
}

// 违反：重写 draw()，加入条件判断
// 改变父类 draw 语义，导致替换父类后行为不一致
func (s square) draw() {
  // 新增条件导致行为差异
  if s.checkArea() {
    fmt.Println("Drawing Square. area:", s.area())
  } else {
    fmt.Println("Don't draw square")
  }
}

// 该额外校验导致 draw 行为改变
func (s square) checkArea() bool {
  return s.area() <= 100
}

func (s square) area() float64 {
  return s.side * s.side
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
  var square1 Shape = square{side: 6}
  var square2 Shape = square{side: 12}
  var rectangle1 Shape = rectangle{width: 8, height: 5}
  var rectangle2 Shape = rectangle{width: 9, height: 6}

  square1.draw()
  square2.draw()
  rectangle1.draw()
  rectangle2.draw()
}

/**
jarry@jarrys-MBP go % go run LiskovSubstitutionBadExample.go 
Drawing Square. area: 36
Don't draw square
Drawing Shape. area: 40
Drawing Shape. area: 54
*/
