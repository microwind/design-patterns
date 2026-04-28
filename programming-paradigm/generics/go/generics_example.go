package main

import (
	"cmp"
	"fmt"
)

// 泛型结构体示例
type GenericsExample[T any] struct {
	value T
}

// 泛型构造函数
func NewGenericsExample[T any](value T) *GenericsExample[T] {
	return &GenericsExample[T]{value: value}
}

// 泛型方法
func (g *GenericsExample[T]) GetValue() T {
	return g.value
}

func (g *GenericsExample[T]) SetValue(value T) {
	g.value = value
}

// 泛型函数 - 打印数组
func PrintArray[T any](array []T) {
	for _, element := range array {
		fmt.Print(element, " ")
	}
	fmt.Println()
}

// 泛型函数 - 查找最大值（需要约束）
func FindMax[T cmp.Ordered](slice []T) T {
	if len(slice) == 0 {
		var zero T
		return zero
	}
	
	max := slice[0]
	for i := 1; i < len(slice); i++ {
		if slice[i] > max {
			max = slice[i]
		}
	}
	return max
}

// 泛型函数 - 创建切片
func CreateSlice[T any](elements ...T) []T {
	return elements
}

// 泛型函数 - 过滤切片
func Filter[T any](slice []T, predicate func(T) bool) []T {
	var result []T
	for _, item := range slice {
		if predicate(item) {
			result = append(result, item)
		}
	}
	return result
}

// 泛型函数 - 映射切片
func Map[T, U any](slice []T, transform func(T) U) []U {
	result := make([]U, len(slice))
	for i, item := range slice {
		result[i] = transform(item)
	}
	return result
}

// 泛型接口示例
type Comparable[T any] interface {
	Compare(other T) int
}

// 泛型约束示例
type Number interface {
	~int | ~int64 | ~float64
}

// 泛型函数 - 数值计算
func Sum[T Number](numbers []T) T {
	var total T
	for _, num := range numbers {
		total += num
	}
	return total
}

// 泛型函数 - 交换两个值
func Swap[T any](a, b *T) {
	*a, *b = *b, *a
}

// 泛型结构体 - 栈
type Stack[T any] struct {
	elements []T
}

func NewStack[T any]() *Stack[T] {
	return &Stack[T]{elements: make([]T, 0)}
}

func (s *Stack[T]) Push(element T) {
	s.elements = append(s.elements, element)
}

func (s *Stack[T]) Pop() (T, bool) {
	if len(s.elements) == 0 {
		var zero T
		return zero, false
	}
	
	index := len(s.elements) - 1
	element := s.elements[index]
	s.elements = s.elements[:index]
	return element, true
}

func (s *Stack[T]) IsEmpty() bool {
	return len(s.elements) == 0
}

func main() {
	// 泛型结构体使用
	stringBox := NewGenericsExample("Hello Generics")
	fmt.Printf("String value: %s\n", stringBox.GetValue())
	
	intBox := NewGenericsExample(42)
	fmt.Printf("Integer value: %d\n", intBox.GetValue())
	
	// 泛型函数使用
	stringArray := []string{"Apple", "Banana", "Orange"}
	fmt.Print("String array: ")
	PrintArray(stringArray)
	
	intArray := []int{1, 5, 3, 9, 2}
	fmt.Print("Integer array: ")
	PrintArray(intArray)
	
	// 泛型函数查找最大值
	fmt.Printf("Max number: %d\n", FindMax(intArray))
	fmt.Printf("Max string: %s\n", FindMax(stringArray))
	
	// 泛型集合
	stringList := CreateSlice("Hello", "World", "Generics")
	fmt.Printf("String list: %v\n", stringList)
	
	intList := CreateSlice(1, 2, 3, 4, 5)
	fmt.Printf("Integer list: %v\n", intList)
	
	// 泛型函数过滤
	evenNumbers := Filter(intList, func(n int) bool { return n%2 == 0 })
	fmt.Printf("Even numbers: %v\n", evenNumbers)
	
	// 泛型函数映射
	strLengths := Map(stringList, func(s string) int { return len(s) })
	fmt.Printf("String lengths: %v\n", strLengths)
	
	// 泛型约束示例
	integers := []int{1, 2, 3, 4, 5}
	fmt.Printf("Sum of numbers: %d\n", Sum(integers))
	
	floats := []float64{1.1, 2.2, 3.3}
	fmt.Printf("Sum of floats: %.1f\n", Sum(floats))
	
	// 泛型函数交换
	x, y := 10, 20
	fmt.Printf("Before swap: x=%d, y=%d\n", x, y)
	Swap(&x, &y)
	fmt.Printf("After swap: x=%d, y=%d\n", x, y)
	
	// 泛型栈使用
	intStack := NewStack[int]()
	intStack.Push(1)
	intStack.Push(2)
	intStack.Push(3)
	
	for !intStack.IsEmpty() {
		if val, ok := intStack.Pop(); ok {
			fmt.Printf("Popped: %d\n", val)
		}
	}
	
	// 字符串栈
	stringStack := NewStack[string]()
	stringStack.Push("First")
	stringStack.Push("Second")
	
	for !stringStack.IsEmpty() {
		if val, ok := stringStack.Pop(); ok {
			fmt.Printf("Popped: %s\n", val)
		}
	}
}

/*
编译和运行：
go run generics_example.go

输出结果：
String value: Hello Generics
Integer value: 42
String array: Apple Banana Orange 
Integer array: 1 5 3 9 2 
Max number: 9
Max string: Orange
String list: [Hello World Generics]
Integer list: [1 2 3 4 5]
Even numbers: [2 4]
String lengths: [5 5 8]
Sum of numbers: 15
Sum of floats: 6.6
Before swap: x=10, y=20
After swap: x=20, y=10
Popped: 3
Popped: 2
Popped: 1
Popped: Second
Popped: First
*/
