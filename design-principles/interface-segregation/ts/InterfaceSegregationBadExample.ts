/**
 * 接口隔离原则 - 反例
 * 这个例子违反了接口隔离原则，为了方便理解把相关类放在一起。
 * 1. 接口过于庞大，比较臃肿，职责不单一。
 * 2. 实现对象依赖了并不需要的接口方法，在实现时浪费。
 * 对比：正例把接口拆分为多个小接口，类只实现自己需要的方法。
 */
// 违规点：一个类实现所有设备控制接口，导致方法臃肿
class AllDeviceController {
  turnOnTV(): void {
    console.log('Turn on TV');
  }

  turnOffTV(): void {
    console.log('Turn off TV');
  }

  adjustTVVolume(volume: number): void {
    console.log(`Adjust TV volume to ${volume}`);
  }

  changeTVChannel(channel: number): void {
    console.log(`Change TV channel to ${channel}`);
  }

  // 对只控制电视的实现来说，这些灯光方法是冗余的
  turnOnLight(): void {
    console.log('Turn on Light');
  }

  turnOffLight(): void {
    console.log('Turn off Light');
  }

  changeLightColor(color: string): void {
    console.log(`Change Light color to ${color}`);
  }
}

const controller = new AllDeviceController();
controller.turnOnTV();
controller.adjustTVVolume(10);
controller.changeTVChannel(2);
controller.turnOffTV();
controller.turnOnLight();
controller.changeLightColor('Red');
controller.turnOffLight();

/**
jarry@jarrys-MBP ts % ts-node InterfaceSegregationBadExample.ts 
Turn on TV
Adjust TV volume to 10
Change TV channel to 2
Turn off TV
Turn on Light
Change Light color to Red
Turn off Light
*/
