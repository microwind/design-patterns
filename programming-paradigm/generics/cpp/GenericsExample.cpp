#include <iostream>
#include <vector>
#include <algorithm>
#include <string>
#include <type_traits>

// 模板类示例
template<typename T>
class GenericsExample {
private:
    T value;
    
public:
    // 模板构造函数
    GenericsExample(T value) : value(value) {}
    
    // 模板方法
    T getValue() const { return value; }
    void setValue(T val) { value = val; }
    
    // 静态模板方法
    template<typename E>
    static void printArray(const std::vector<E>& array) {
        for (const auto& element : array) {
            std::cout << element << " ";
        }
        std::cout << std::endl;
    }
    
    // 模板方法查找最大值
    template<typename U>
    static U findMax(const std::vector<U>& container) {
        if (container.empty()) {
            return U{};
        }
        
        auto maxIt = std::max_element(container.begin(), container.end());
        return *maxIt;
    }
    
    // 模板集合示例
    template<typename... Args>
    static std::vector<T> createVector(Args... args) {
        return std::vector<T>{args...};
    }
    
    // 模板特化示例
    template<typename U>
    void printTypeInfo() {
        std::cout << "Type: " << typeid(U).name() << std::endl;
    }
};

// 独立的模板函数
template<typename T>
void printArray(const T* array, int size) {
    for (int i = 0; i < size; ++i) {
        std::cout << array[i] << " ";
    }
    std::cout << std::endl;
}

// 模板函数比较两个值
template<typename T>
T findMax(const T* array, int size) {
    if (size <= 0) {
        return T{};
    }
    
    T max = array[0];
    for (int i = 1; i < size; ++i) {
        if (array[i] > max) {
            max = array[i];
        }
    }
    return max;
}

// 模板约束示例（C++20 concepts）
#if __cplusplus >= 202002L
template<typename T>
concept Comparable = requires(T a, T b) {
    { a < b } -> std::convertible_to<bool>;
    { a > b } -> std::convertible_to<bool>;
};

template<Comparable T>
T findMaxWithConcept(const std::vector<T>& container) {
    if (container.empty()) {
        return T{};
    }
    
    auto maxIt = std::max_element(container.begin(), container.end());
    return *maxIt;
}
#endif

// 模板特化示例
template<>
template<>
void GenericsExample<std::string>::printTypeInfo<std::string>() {
    std::cout << "Type: std::string (specialized)" << std::endl;
}

int main() {
    // 模板类使用
    GenericsExample<std::string> stringBox("Hello Generics");
    std::cout << "String value: " << stringBox.getValue() << std::endl;
    
    GenericsExample<int> integerBox(42);
    std::cout << "Integer value: " << integerBox.getValue() << std::endl;
    
    // 模板方法使用
    std::vector<std::string> stringArray = {"Apple", "Banana", "Orange"};
    std::cout << "String array: ";
    GenericsExample<std::string>::printArray(stringArray);
    
    std::vector<int> intArray = {1, 5, 3, 9, 2};
    std::cout << "Integer array: ";
    GenericsExample<int>::printArray(intArray);
    
    // 模板方法查找最大值
    std::cout << "Max number: " << GenericsExample<int>::findMax(intArray) << std::endl;
    std::cout << "Max string: " << GenericsExample<std::string>::findMax(stringArray) << std::endl;
    
    // 模板集合示例
    auto stringList = GenericsExample<std::string>::createVector("Hello", "World", "Generics");
    std::cout << "String list: ";
    GenericsExample<std::string>::printArray(stringList);
    
    auto intList = GenericsExample<int>::createVector(1, 2, 3, 4, 5);
    std::cout << "Integer list: ";
    GenericsExample<int>::printArray(intList);
    
    // 独立模板函数使用
    std::string fruits[] = {"Apple", "Banana", "Orange"};
    std::cout << "Fruits array: ";
    printArray(fruits, 3);
    
    int numbers[] = {1, 5, 3, 9, 2};
    std::cout << "Numbers array: ";
    printArray(numbers, 5);
    
    std::cout << "Max fruits: " << findMax(fruits, 3) << std::endl;
    std::cout << "Max numbers: " << findMax(numbers, 5) << std::endl;
    
    // 模板特化示例
    stringBox.printTypeInfo<std::string>();
    integerBox.printTypeInfo<int>();
    
#if __cplusplus >= 202002L
    // C++20 concepts 示例
    std::cout << "Max with concept: " << findMaxWithConcept(intArray) << std::endl;
#endif
    
    return 0;
}

/*
编译和运行：
g++ -std=c++17 -o generics GenericsExample.cpp
./generics

输出结果：
String value: Hello Generics
Integer value: 42
String array: Apple Banana Orange 
Integer array: 1 5 3 9 2 
Max number: 9
Max string: Orange
String list: Hello World Generics 
Integer list: 1 2 3 4 5 
Fruits array: Apple Banana Orange 
Numbers array: 1 5 3 9 2 
Max fruits: Orange
Max numbers: 9
Type: std::string (specialized)
Type: i
Max with concept: 9
*/
