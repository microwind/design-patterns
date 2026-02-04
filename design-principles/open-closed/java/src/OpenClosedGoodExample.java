package src;

/**
 * 这个例子符合开闭原则。
 * 1. 通过抽象类定义车辆接口，具体实现类继承抽象类。
 * 2. 车辆工厂通过抽象类接收具体实现，无需修改工厂逻辑即可支持新的车辆类型。
 * 3. 当有新的车辆类型加入时，只需扩展新的实现类，不需要修改现有代码。
 */
public class OpenClosedGoodExample {
    public OpenClosedGoodExample() {
        return;
    }

    /**
     * 车辆抽象类，用于具体对象继承或实现，便于统一约束和扩展
     */
    public abstract class AbstractVehicle {
        public String name;
        public int type;
        public int weight;
        protected abstract boolean create();
        public abstract String getName();
    }

    /**
     * 汽车类继承自抽象机动车类
     */
    public class Car extends AbstractVehicle {

        public String name = "car";
        private int type = 1;
        public int weight = 2500;

        public Car(String name) {
            this.name = name;
        }

        @Override
        public boolean create() {
            System.out.println("car has been produced: " + " " + this.type + " " + this.name + " " + this.weight);
            return true;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    /**
     * 巴士类继承自抽象机动车类
     */
    public class Bus extends AbstractVehicle {

        public String name = "bus";
        private int type = 2;
        public int weight = 15000;

        public Bus(String name) {
            this.name = name;
        }

        @Override
        public boolean create() {
            System.out.println("bus has been produced: " + " " + this.type + " " + this.name + " " + this.weight);
            return true;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    /**
     * 车辆制造工厂，关联抽象汽车类，调用具体车辆的制造方法
     * 可以参照设计模式中的抽象工厂和工厂方法来获取具体工厂
     * 总之要将具体对象的方法与工厂调用逻辑区分开
     */
    public class VehicleFactory {
        public AbstractVehicle createVehicle(AbstractVehicle vehicle) {
            // 从工厂里调用具体对象的方法，避免通过if else 进行判断获取某个对象。
            // 当增加其他具体对象时不用修改这里的逻辑，
            // 而是通过扩展新增对象来实现。因此对修改关闭，对扩展开放。
            vehicle.create();
            // 可以返回具体对象或者调用基础方法
            return vehicle;
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing OpenClosedGoodExample...");
        OpenClosedGoodExample example = new OpenClosedGoodExample();
        VehicleFactory factory = example.new VehicleFactory();
        
        // Test Car
        Car car = example.new Car("BMW");
        factory.createVehicle(car);
        System.out.println("Car name: " + car.getName());
        
        // Test Bus
        Bus bus = example.new Bus("Volvo");
        factory.createVehicle(bus);
        System.out.println("Bus name: " + bus.getName());
    }
}

