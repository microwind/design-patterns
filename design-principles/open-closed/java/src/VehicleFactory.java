
package src;

/**
 * 车辆制造工厂，关联抽象汽车类，调用具体车辆的制造方法
 * 可以参照设计模式中的抽象工厂和工厂方法来获取具体工厂
 * 总之要将具体对象的方法与工厂调用逻辑区分开
 */
public class VehicleFactory {
    public static AbstractVehicle createVehicle(AbstractVehicle vehicle) {
        // 从工厂里调用具体对象的方法，避免通过if else 进行判断获取某个对象。
        // 当增加其他具体对象时不用修改这里的逻辑，
        // 而是通过扩展新增对象来实现。因此对修改关闭，对扩展开放。
        vehicle.create();
        // 执行创建方法再返回具体对象
        return vehicle;
    }
}