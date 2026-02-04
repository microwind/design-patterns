package src;

/**
 * 这个例子符合合成复用原则。
 * 1. 通过组合来替代继承进行代码复用，将有关联的类聚合到业务类中。
 * 2. 聚合了Person类，而不是通过继承获得其属性和方法。
 * 3. 这种方式更加灵活，便于扩展和维护。
 */
public class CompositeReuseGoodExample {
    public CompositeReuseGoodExample() {
        return;
    }

    /**
     * 人物类
     */
    public class Person {
        public String name;
        public int age;
        public int gender;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    /**
     * 雇员抽象类，供其他角色继承
     * 聚合了人物类
     */
    public abstract class Employee {
        // 聚合人物类，通过聚合方式更加松耦合，利于扩展
        Person person;
        public int id;
        public String title;

        public boolean work() {
            return true;
        }

    }

    /**
     * 工程师类继承雇员类
     */
    public class Engineer extends Employee {

        public Engineer(int id, String title, Person person) {
            this.id = id;
            this.title = title;
            this.person = person;
        }

        @Override
        public boolean work() {
            System.out.println("Engineer is working." + " id = " + this.id
                    + ", title = " + this.title + " name = " + person.getName() + ", age = " + person.getAge());
            return true;
        }
    }

    /**
     * 管理者类继承雇员类
     */
    public class Manager extends Employee {

        public Manager(int id, String title, Person person) {
            this.id = id;
            this.title = title;
            this.person = person;
        }

        @Override
        public boolean work() {
            System.out.println("Manager is working." + " id = " + this.id
                    + ", title = " + this.title + " name = " + person.getName() + ", age = " + person.getAge());
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing CompositeReuseGoodExample...");
        CompositeReuseGoodExample example = new CompositeReuseGoodExample();
        
        // Create Person objects
        Person person1 = example.new Person("Tom", 25);
        Person person2 = example.new Person("Jerry", 45);
        
        // Test Engineer
        Engineer engineer = example.new Engineer(1001, "senior engineer", person1);
        engineer.work();
        
        // Test Manager
        Manager manager = example.new Manager(2002, "advanced director", person2);
        manager.work();
    }
}
