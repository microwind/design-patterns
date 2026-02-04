package src;

/**
 * 这个例子违反了组合复用原则，因为采用了继承而不是组合。
 * 1. 人的身份会有多重角色，不是适合继承人，如果是物种角度可以是继承，比如男、女继承人。
 * 2. 采用继承会使得不够灵活，修改起来很麻烦，例如一个人既是经理也是雇员。
 */
public class CompositeReuseBadExample {
    public CompositeReuseBadExample() {
        return;
    }

    // 顶级抽象父类
    public abstract class Person {
        public String name;
        public int age;
        public int gender;

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    // 雇员父类继承了Person类，这里违反了组合复用原则，不利于扩展
    public class Employee extends Person {
        public int id;
        public String title;

        public boolean work() {
            return true;
        }
    }

    // 具体职位类
    public class Engineer extends Employee {

        public Engineer(String name, int age, int id, String title) {
            this.name = name;
            this.age = age;
            this.id = id;
            this.title = title;
        }

        @Override
        public boolean work() {
            System.out.println("Engineer is working." + " id = " + this.id
                    + ", title = " + this.title + " name = " + this.getName() + ", age = " + this.getAge());
            return true;
        }
    }

    // 具体职位类
    public class Manager extends Employee {

        public Manager(String name, int age, int id, String title) {
            this.name = name;
            this.age = age;
            this.id = id;
            this.title = title;
        }

        @Override
        public boolean work() {
            System.out.println("Manager is working." + " id = " + this.id
                    + ", title = " + this.title + " name = " + this.getName() + ", age = " + this.getAge());
            return true;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Testing CompositeReuseBadExample...");
        CompositeReuseBadExample example = new CompositeReuseBadExample();
        
        // Test Engineer
        Engineer engineer = example.new Engineer("Tom", 25, 1001, "senior engineer");
        engineer.work();
        
        // Test Manager
        Manager manager = example.new Manager("Jerry", 45, 2002, "advanced director");
        manager.work();
    }
}