#include <stdio.h>

/*
 * 接口隔离原则：
 * 1. 接口建立在最小化抽象上，接口功能单一明确，按照单一职责来建立接口，而不要将接口设计得过于复杂。
 * 2. 客户端不依赖它不需要的接口方法，只依赖它必须的接口方法，也就是最小依赖原则，以此保证代码结构清晰与纯洁性。
 */

// 电器设备的基础控制接口，约定基本的一些控制方法【可选】
typedef struct
{
  void (*turnOn)();
  void (*turnOff)();
} DeviceController;

// Light控制接口，包括TV需要的控制方法，供Light具体对象实现
// 每个具体控制对象有其自身的接口，不跟其他对象混在一起
typedef struct
{
  DeviceController deviceController;
  void (*changeLightColor)(const char *color);
} LightController;

// TV控制接口，包括TV需要的控制方法，供TV具体对象实现
// 每个具体控制对象有其自身的接口，不跟其他对象混在一起
typedef struct
{
  DeviceController deviceController;
  void (*adjustTVVolume)(int volume);
  void (*changeTVChannel)(int channel);
} TVController;

// Light实现类，实现Light控制接口的全部方法
void turnOnLight()
{
  // 执行灯光打开操作
  printf("Turn on Light\n");
}

void turnOffLight()
{
  // 执行灯光关闭操作
  printf("Turn off Light\n");
}

void changeLightColor(const char *color)
{
  // 执行灯光颜色切换
  printf("Change Light color to %s\n", color);
}

// TV实现类，实现TV控制接口的全部方法
void turnOnTV()
{
  printf("Turn on TV\n");
  // 执行电视打开操作
}

void turnOffTV()
{
  printf("Turn off TV\n");
  // 执行电视关闭操作
}

void adjustTVVolume(int volume)
{
  // 执行调节音量操作
  printf("Adjust TV volume to %d\n", volume);
}

void changeTVChannel(int channel)
{
  // 执行频道切换操作
  printf("Change TV channel to %d\n", channel);
}

// 测试验证
int main()
{
  // 创建TV对象
  TVController tv = {
      {&turnOnTV, &turnOffTV},
      &adjustTVVolume,
      &changeTVChannel};
  tv.deviceController.turnOn();
  tv.adjustTVVolume(10); // 这里的方法仅对于TV来说
  tv.changeTVChannel(2); // 这里的方法仅对于TV来说
  tv.deviceController.turnOff();

  // 控制灯光
  LightController light = {
      {&turnOnLight, &turnOffLight},
      &changeLightColor};
  light.deviceController.turnOn();
  light.changeLightColor("Red"); // 这里的方法仅对于Light来说
  light.deviceController.turnOff();

  return 0;
}

/**
jarry@jarrys-MBP c % gcc interface_segregation_good_example.c 
jarry@jarrys-MBP c % ./a.out 
Turn on TV
Adjust TV volume to 10
Change TV channel to 2
Turn off TV
Turn on Light
Change Light color to Red
Turn off Light
*/
