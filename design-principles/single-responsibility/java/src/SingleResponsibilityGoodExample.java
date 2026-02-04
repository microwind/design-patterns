package src;

/**
 * 这个例子符合单一职责原则。
 * 1. 分别建立三个类，一个负责订单业务处理(OrderProcessor)，一个负责校验订单(OrderValidator)，一个负责保存数据(OrderDao)，各司其职。
 * 2. 由处理订单来负责调用工具类，订单业务逻辑修改不会影响到工具类。
 * 3. 工具类修改也不会影响到订单业务处理。
 * 4. 职责是否足够单一，要根据具体场景而异，不同情形下采用不同的设计，主要的目标是便于理解、扩展和维护。
 */
public class SingleResponsibilityGoodExample {
    public SingleResponsibilityGoodExample() {
        return;
    }

    // 订单处理器类，负责订单业务处理
    public class OrderProcessor {
        private OrderValidator orderValidator = new OrderValidator();
        private OrderDao orderDao = new OrderDao();

        public OrderProcessor() {
        }

        // 订单处理逻辑方法
        public Object processOrder(Long orderId) {
            System.out.println("oder ID：" + orderId);
            // 1. 先验证订单，调用校验类
            if (!orderValidator.validateId(orderId)) {
                System.out.println("order validate id failed.");
                return false;
            }

            if (!orderValidator.validateTime(System.currentTimeMillis())) {
                System.out.println("order validate time failed.");
                return false;
            }

            // 2. 订单数据其他逻辑处理
            if (orderId % 2 == 0) {
                System.out.println("order data processing.");
            }

            // 3. 再保存订单到数据库，调用数据库类
            System.out.println("order save to DB.");
            orderDao.saveOrder(orderId);

            return true;
        }
    }

    /**
     * 订单校验类，校验订单的合法性和有效性等
     */
    public class OrderValidator {

        // 校验订单逻辑，最好别放在订单处理类中
        public boolean validateId(Long orderId) {
            // doSomething
            if (orderId % 2 == 1) {
                return false;
            }
            return true;
        }

        public boolean validateTime(Long time) {
            // doSomething
            return true;
        }
    }

    /**
     * Order数据库访问类，负责处理订单的CRUD操作
     */
    public class OrderDao {

        // 删除订单
        protected boolean deleteOrder(Long orderId) {
            // doSomething
            return true;
        }

        // 保存订单到数据库
        protected boolean saveOrder(Long orderId) {
            if (orderId % 2 == 0) {
                System.out.println("data saving.");
            }
            System.out.println("data save done.");
            return true;
        }

    }

    public static void main(String[] args) {
        System.out.println("Testing SingleResponsibilityGoodExample...");
        SingleResponsibilityGoodExample example = new SingleResponsibilityGoodExample();
        OrderProcessor processor = example.new OrderProcessor();
        processor.processOrder(1001L);
        processor.processOrder(1002L);
    }
}
