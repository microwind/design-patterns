import java.util.ArrayList;
import java.util.List;

// 泛型类示例
public class GenericsExample<T> {
    private T value;
    
    // 泛型构造函数
    public GenericsExample(T value) {
        this.value = value;
    }
    
    // 泛型方法
    public T getValue() {
        return value;
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    // 静态泛型方法
    public static <E> void printArray(E[] array) {
        for (E element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }
    
    // 泛型方法比较两个值
    public static <T extends Comparable<T>> T findMax(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        
        T max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i].compareTo(max) > 0) {
                max = array[i];
            }
        }
        return max;
    }
    
    // 泛型集合示例
    public static <T> List<T> createList(T... elements) {
        List<T> list = new ArrayList<>();
        for (T element : elements) {
            list.add(element);
        }
        return list;
    }
    
    public static void main(String[] args) {
        // 泛型类使用
        GenericsExample<String> stringBox = new GenericsExample<>("Hello Generics");
        System.out.println("String value: " + stringBox.getValue());
        
        GenericsExample<Integer> integerBox = new GenericsExample<>(42);
        System.out.println("Integer value: " + integerBox.getValue());
        
        // 泛型方法使用
        String[] stringArray = {"Apple", "Banana", "Orange"};
        System.out.print("String array: ");
        printArray(stringArray);
        
        Integer[] intArray = {1, 5, 3, 9, 2};
        System.out.print("Integer array: ");
        printArray(intArray);
        
        // 泛型方法查找最大值
        Integer[] numbers = {1, 5, 3, 9, 2};
        System.out.println("Max number: " + findMax(numbers));
        
        String[] fruits = {"Apple", "Banana", "Orange"};
        System.out.println("Max string: " + findMax(fruits));
        
        // 泛型集合
        List<String> stringList = createList("Hello", "World", "Generics");
        System.out.println("String list: " + stringList);
        
        List<Integer> intList = createList(1, 2, 3, 4, 5);
        System.out.println("Integer list: " + intList);
    }
}

/*
输出结果：
String value: Hello Generics
Integer value: 42
String array: Apple Banana Orange 
Integer array: 1 5 3 9 2 
Max number: 9
Max string: Orange
String list: [Hello, World, Generics]
Integer list: [1, 2, 3, 4, 5]
*/
