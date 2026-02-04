/**
 * 接口隔离原则 - 反例
 * 这个例子违反了接口隔离原则，为了方便理解把相关类放在一起。
 * 1. 接口过于庞大，比较臃肿，职责不单一。
 * 2. 实现对象依赖了并不需要的接口方法，在实现时浪费。
 * 对比：正例把接口拆分为多个小接口，类只实现自己需要的方法。
 */
package main

import "fmt"

// 违规点：接口过大，控制类被迫实现不需要的方法
type AllDeviceController struct{}

func (c AllDeviceController) TurnOnTV() {
  fmt.Println("Turn on TV")
}

func (c AllDeviceController) TurnOffTV() {
  fmt.Println("Turn off TV")
}

func (c AllDeviceController) AdjustTVVolume(volume int) {
  fmt.Printf("Adjust TV volume to %d\n", volume)
}

func (c AllDeviceController) ChangeTVChannel(channel int) {
  fmt.Printf("Change TV channel to %d\n", channel)
}

// 对只控制电视的实现来说，这些灯光方法是冗余的
func (c AllDeviceController) TurnOnLight() {
  fmt.Println("Turn on Light")
}

func (c AllDeviceController) TurnOffLight() {
  fmt.Println("Turn off Light")
}

func (c AllDeviceController) ChangeLightColor(color string) {
  fmt.Printf("Change Light color to %s\n", color)
}

func main() {
  controller := AllDeviceController{}
  controller.TurnOnTV()
  controller.AdjustTVVolume(10)
  controller.ChangeTVChannel(2)
  controller.TurnOffTV()
  controller.TurnOnLight()
  controller.ChangeLightColor("Red")
  controller.TurnOffLight()
}

/**
jarry@jarrys-MBP go % go run InterfaceSegregationBadExample.go 
Turn on TV
Adjust TV volume to 10
Change TV channel to 2
Turn off TV
Turn on Light
Change Light color to Red
Turn off Light
*/
