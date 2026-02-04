package src;

/**
 * 这个例子违反了接口隔离原则，为了方面理解，把全部类放在了一起。
 * 1. 接口过于庞大，比较臃肿，职责不单一。
 * 2. 实现对象依赖了并不需要的接口方法，在实现时浪费。
 */
public class InterfaceSegregationBadExample {
    public InterfaceSegregationBadExample() {
        return;
    }

    // 定义设备控制接口，把各种控制都汇总在一个大接口中
    public interface DeviceController {
        public void turnOnTV();

        public void turnOffTV();

        public void adjustTVVolume(int volume);

        public void changeTVChannel(int channel);

        public void turnOnLight();

        public void turnOffLight();

        public void changeLightColor(String color);
    }

    // 定义具体全部接口实现类，里面方法太多，将不同控制混杂在了一起
    // 如果按实体分为TV实现类和Light实现类等，那么接口是一个，各实现类需要实现的方法又太多
    public class AllDeviceController implements DeviceController {
        @Override
        public void turnOnTV() {
            System.out.println("Turn on TV");
            // 执行电视打开操作
        }

        @Override
        public void turnOffTV() {
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

        @Override
        public void turnOnLight() {
            // 执行灯光打开操作
            System.out.println("Turn on Light");
        }

        @Override
        public void turnOffLight() {
            // 执行灯光关闭操作
            System.out.println("Turn off Light");
        }

        @Override
        public void changeLightColor(String color) {
            // 执行灯光颜色切换
            System.out.println("Change Light color to " + color);
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Testing InterfaceSegregationBadExample...");
        InterfaceSegregationBadExample example = new InterfaceSegregationBadExample();
        DeviceController controller = example.new AllDeviceController();
        
        // Test TV controls
        controller.turnOnTV();
        controller.adjustTVVolume(10);
        controller.changeTVChannel(2);
        controller.turnOffTV();
        
        // Test Light controls
        controller.turnOnLight();
        controller.changeLightColor("Red");
        controller.turnOffLight();
    }
}