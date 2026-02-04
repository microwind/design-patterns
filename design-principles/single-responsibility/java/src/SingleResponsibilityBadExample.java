package src;

/**
 * 这个例子违反了单一职责原则。
 * 1. 订单处理类实现了订单校验以及保存数据库的两种逻辑。
 * 2. 一旦订单条件有修改或保存数据库方式有变更都需要改动此类。
 */
public class SingleResponsibilityBadExample {
    public SingleResponsibilityBadExample() {
        return;
    }

    // 一个类负责订单处理、校验以及保存多重职责
    public class OrderProcessor {

        // 订单处理逻辑方法
        public Object processOrder(Long orderId) {
            System.out.println("oder ID：" + orderId);
            // 1. 先验证订单
            if (!this.validateId(orderId)) {
                System.out.println("order validate id failed.");
                return false;
            }

            if (!this.validateTime(System.currentTimeMillis())) {
                System.out.println("order validate time failed.");
                return false;
            }

            // 2. 订单数据其他逻辑处理
            if (orderId % 2 == 0) {
                System.out.println("order data processing.");
            }

            // 3. 再保存订单到数据库
            System.out.println("order save to DB.");
            this.saveOrder(orderId);
            // 或则删除订单
            // this.deleteOrder(orderId);

            return true;
        }

        // 校验订单逻辑，最好别放在订单处理类中
        private boolean validateId(Long orderId) {
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

        // 保存订单到数据库逻辑，不应该放在订单处理类中
        private boolean saveOrder(Long orderId) {
            if (orderId % 2 == 0) {
                System.out.println("order saving.");
            }
            System.out.println("order save done.");
            return true;
        }

        // 删除订单，也不要直接放在订单处理类
        protected boolean deleteOrder(Long orderId) {
            // doSomething
            return true;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Testing SingleResponsibilityBadExample...");
        SingleResponsibilityBadExample example = new SingleResponsibilityBadExample();
        OrderProcessor processor = example.new OrderProcessor();
        processor.processOrder(1001L);
        processor.processOrder(1002L);
    }
}
