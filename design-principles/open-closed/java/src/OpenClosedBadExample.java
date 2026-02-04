package src;

/**
 * 这个例子违反了开闭原则，为了演示方便把其他类作为子类放在一起。
 * 1. 车辆制造类将具体创建方法放在了一起，不利于扩展。
 * 2. 一旦有新的车辆类型加入就需要不断修改车辆建造类。
 */
public class OpenClosedBadExample {
    public OpenClosedBadExample() {
        return;
    }

    public class Vehicle {
        public String name;
        public int type;
        public int weight;

        public String getName() {
            return this.name;
        }
    }

    public class Car extends Vehicle {
        public String name = "car";
        public int type = 1;
        public int weight = 2500;

        public Car(String name) {
            this.name = name;
            super.type = this.type;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public class Bus extends Vehicle {
        public String name = "bus";
        public int type = 2;
        public int weight = 15000;

        public Bus(String name) {
            this.name = name;
            super.type = this.type;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public class VehicleFactory {
        public VehicleFactory() {
            return;
        }

        public Vehicle createVehicle(Vehicle vehicle) {
            // 从工厂里调用具体对象的方法，避免通过if else/switch 判断来区分对象。
            // 当增加其他具体对象时就会需要修改这里的方法，这样就就违背了对修改关闭，对扩展开放原则。
            // 好的做法是这个方法里具体对象直接往下传递，不做逻辑处理，从而方便扩展。
            switch (vehicle.type) {
                case 1:
                    createCar(vehicle);
                    break;
                case 2:
                    createBus(vehicle);
                    break;
                // 如果新增其他对象则需要不断修改
                // case 3:
                //     createVan(vehicle);
                // break;
                default:
                    break;
            }
            // 可以返回具体对象或者调用基础方法
            return vehicle;
        }

        public Vehicle createCar(Vehicle vehicle) {
            // doSomething
            Car car = (Car) vehicle;
            System.out.println("car has been produced: " + " " + car.type + " " + car.name + " " + car.weight);
            return vehicle;
        }

        public Vehicle createBus(Vehicle vehicle) {
            // doSomething
            Bus bus = (Bus) vehicle;
            System.out.println("bus has been produced: " + " " + bus.type + " " + bus.name + " " + bus.weight);
            return vehicle;
        }

    }
    
    public static void main(String[] args) {
        System.out.println("Testing OpenClosedBadExample...");
        OpenClosedBadExample example = new OpenClosedBadExample();
        VehicleFactory factory = example.new VehicleFactory();
        
        // Test creating a car
        Vehicle car = example.new Car("BMW");
        factory.createVehicle(car);
        
        // Test creating a bus
        Vehicle bus = example.new Bus("Volvo");
        factory.createVehicle(bus);
    }
}