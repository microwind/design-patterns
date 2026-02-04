package src;

/**
 * 这个例子符合里氏替换原则。
 * 1. 子类不重写父类的非抽象方法，保持行为一致性。
 * 2. 通过新增方法来扩展功能，而不是改变父类的行为。
 * 3. 这样子类可以安全地替换父类，不会影响程序的正确性。
 */
public class LiskovSubstitutionGoodExample {
    public LiskovSubstitutionGoodExample() {
        return;
    }

    /**
     * 抽象图形父类，抽象方法供重写，非抽象方法尽量不去覆盖
     */
    public abstract class Shape {
        public void draw() {
            System.out.println("Drawing Shape. area:" + this.area());
        }

        public abstract double area();
    }

    /**
     * Square对象类，继承父类Shape
     */
    public class Square extends Shape {
        private double side;

        public Square(double side) {
            this.side = side;
        }

        // 不重写父类的draw方法
        // 另外起名，或通过重载得到新方法
        // public void draw(int type) {
        public void drawSquare() {
            if (checkArea()) {
                System.out.println("Haha Drawing Square. area:" + this.area());
            } else {
                System.out.println("Don't draw square");
            }
        }

        public boolean checkArea() {
            if (this.area() > 100) {
                return false;
            }
            return true;
        }

        @Override
        public double area() {
            return side * side;
        }
    }

    /**
     * Rectangle类，继承父类
     */
    public class Rectangle extends Shape {
        private double width;
        private double height;

        public Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        // 这里没有覆盖父类的draw()方法

        @Override
        public double area() {
            return width * height;
        }
    }

    // 测试方法：接受父类类型参数
    public void processShape(Shape shape) {
        shape.draw();  // 期望所有Shape都能正常绘制
    }

    public static void main(String[] args) {
        System.out.println("Testing LiskovSubstitutionGoodExample...");
        LiskovSubstitutionGoodExample example = new LiskovSubstitutionGoodExample();
        
        // Test Rectangle
        Shape rectangle = example.new Rectangle(10, 20);
        rectangle.draw();
        
        // Test Square
        Shape square = example.new Square(15);
        square.draw();
        
        // Test Square-specific method
        Square squareObj = example.new Square(5);
        squareObj.drawSquare();
    }
}
