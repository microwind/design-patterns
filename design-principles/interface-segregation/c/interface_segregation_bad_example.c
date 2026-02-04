#include <stdio.h>
/**
 * 接口隔离原则 - 反例
 * 这个例子违反了接口隔离原则，为了方便理解把相关类放在一起。
 * 1. 接口过于庞大，比较臃肿，职责不单一。
 * 2. 实现对象依赖了并不需要的接口方法，在实现时浪费。
 * 对比：正例把接口拆分为多个小接口，类只实现自己需要的方法。
 */

typedef struct {
  int unused;
} AllDeviceController;

void turn_on_tv(AllDeviceController *controller) {
  (void)controller;
  printf("Turn on TV\n");
}

void turn_off_tv(AllDeviceController *controller) {
  (void)controller;
  printf("Turn off TV\n");
}

void adjust_tv_volume(AllDeviceController *controller, int volume) {
  (void)controller;
  printf("Adjust TV volume to %d\n", volume);
}

void change_tv_channel(AllDeviceController *controller, int channel) {
  (void)controller;
  printf("Change TV channel to %d\n", channel);
}

// 对只控制电视的实现来说，这些灯光方法是冗余的
void turn_on_light(AllDeviceController *controller) {
  (void)controller;
  printf("Turn on Light\n");
}

void turn_off_light(AllDeviceController *controller) {
  (void)controller;
  printf("Turn off Light\n");
}

void change_light_color(AllDeviceController *controller, const char *color) {
  (void)controller;
  printf("Change Light color to %s\n", color);
}

int main() {
  AllDeviceController controller = {0};
  turn_on_tv(&controller);
  adjust_tv_volume(&controller, 10);
  change_tv_channel(&controller, 2);
  turn_off_tv(&controller);
  turn_on_light(&controller);
  change_light_color(&controller, "Red");
  turn_off_light(&controller);
  return 0;
}
/**
 * 接口隔离原则 - 反例
 * 这个例子违反了接口隔离原则，为了方便理解把相关类放在一起。
 * 1. 接口过于庞大，比较臃肿，职责不单一。
 * 2. 实现对象依赖了并不需要的接口方法，在实现时浪费。
 * 对比：正例把接口拆分为多个小接口，类只实现自己需要的方法。
 */
