package src;

/**
 * 这个例子符合接口隔离原则。
 * 1. 将设备控制接口拆分为基础接口和具体设备接口。
 * 2. 每个实现类只实现自己需要的接口方法。
 * 3. 避免了实现不需要的方法导致的代码冗余。
 */
public class InterfaceSegregationGoodExample {
    public InterfaceSegregationGoodExample() {
        return;
    }

    /**
     * 电器设备的基础控制接口，约定基本的一些控制方法【可选】
     */
    public interface DeviceController {
        public void turnOn();
        public void turnOff();
    }

    /**
     * Light控制接口，包括TV需要的控制方法，供Light具体对象实现
     * 每个具体控制对象有其自身的接口，不跟其他对象混在一起
     */
    public interface LightController extends DeviceController {
        public void changeLightColor(String color);
    }

    /**
     * TV控制接口，包括TV需要的控制方法，供TV具体对象实现
     * 每个具体控制对象有其自身的接口，不跟其他对象混在一起
     */
    public interface TVController extends DeviceController {
        public void adjustTVVolume(int volume);

        public void changeTVChannel(int channel);
    }

    /**
     * Light实现类，实现Light控制接口的全部方法
     */
    public class Light implements LightController {
        @Override
        public void turnOn() {
            // 执行灯光打开操作
            System.out.println("Turn on Light");
        }

        @Override
        public void turnOff() {
            // 执行灯光关闭操作
            System.out.println("Turn off Light");
        }

        @Override
        public void changeLightColor(String color) {
            // 执行灯光颜色切换
            System.out.println("Change Light color to " + color);
        }
    }

    /**
     * TV实现类，实现TV控制接口的全部方法
     */
    public class TV implements TVController {
        @Override
        public void turnOn() {
            System.out.println("Turn on TV");
            // 执行电视打开操作
        }

        @Override
        public void turnOff() {
            System.out.println("Turn off TV");
            // 执行电视关闭操作
        }

        @Override
        public void adjustTVVolume(int volume) {
            // 执行调节音量操作
            System.out.println("Adjust TV volume to " + volume);
        }

        @Override
        public void changeTVChannel(int channel) {
            // 执行频道切换操作
            System.out.println("Change TV channel to " + channel);
        }

    }

    public static void main(String[] args) {
        System.out.println("Testing InterfaceSegregationGoodExample...");
        InterfaceSegregationGoodExample example = new InterfaceSegregationGoodExample();
        
        // Test Light
        Light light = example.new Light();
        light.turnOn();
        light.changeLightColor("Red");
        light.turnOff();
        
        // Test TV
        TV tv = example.new TV();
        tv.turnOn();
        tv.adjustTVVolume(50);
        tv.changeTVChannel(10);
        tv.turnOff();
    }
}
