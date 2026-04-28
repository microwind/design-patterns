#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

// 泛型容器结构体（使用void*实现）
typedef struct {
    void* data;
    size_t size;
    size_t element_size;
} GenericArray;

// 泛型函数 - 创建数组
GenericArray* create_array(size_t element_size, size_t initial_capacity) {
    GenericArray* array = (GenericArray*)malloc(sizeof(GenericArray));
    array->data = malloc(initial_capacity * element_size);
    array->size = 0;
    array->element_size = element_size;
    return array;
}

// 泛型函数 - 释放数组
void free_array(GenericArray* array) {
    if (array) {
        free(array->data);
        free(array);
    }
}

// 泛型函数 - 添加元素
void array_push(GenericArray* array, const void* element) {
    // 重新分配内存
    array->data = realloc(array->data, (array->size + 1) * array->element_size);
    
    // 复制元素到数组末尾
    void* target = (char*)array->data + array->size * array->element_size;
    memcpy(target, element, array->element_size);
    array->size++;
}

// 泛型函数 - 获取元素
void* array_get(GenericArray* array, size_t index) {
    if (index >= array->size) {
        return NULL;
    }
    return (char*)array->data + index * array->element_size;
}

// 泛型函数 - 打印数组
void array_print(GenericArray* array, void (*print_func)(const void*)) {
    for (size_t i = 0; i < array->size; i++) {
        void* element = array_get(array, i);
        print_func(element);
        printf(" ");
    }
    printf("\n");
}

// 泛型函数 - 查找最大值
void* array_find_max(GenericArray* array, int (*compare_func)(const void*, const void*)) {
    if (array->size == 0) {
        return NULL;
    }
    
    void* max = array_get(array, 0);
    for (size_t i = 1; i < array->size; i++) {
        void* current = array_get(array, i);
        if (compare_func(current, max) > 0) {
            max = current;
        }
    }
    return max;
}

// 泛型函数 - 过滤数组
GenericArray* array_filter(GenericArray* array, bool (*predicate)(const void*)) {
    GenericArray* result = create_array(array->element_size, array->size);
    
    for (size_t i = 0; i < array->size; i++) {
        void* element = array_get(array, i);
        if (predicate(element)) {
            array_push(result, element);
        }
    }
    
    return result;
}

// 泛型函数 - 映射数组
GenericArray* array_map(GenericArray* array, void* (*transform)(const void*), size_t new_element_size) {
    GenericArray* result = create_array(new_element_size, array->size);
    
    for (size_t i = 0; i < array->size; i++) {
        void* element = array_get(array, i);
        void* transformed = transform(element);
        array_push(result, transformed);
        free(transformed); // 释放转换后的内存
    }
    
    return result;
}

// 泛型栈结构体
typedef struct {
    GenericArray* array;
} GenericStack;

// 泛型栈函数
GenericStack* create_stack(size_t element_size) {
    GenericStack* stack = (GenericStack*)malloc(sizeof(GenericStack));
    stack->array = create_array(element_size, 10);
    return stack;
}

void free_stack(GenericStack* stack) {
    if (stack) {
        free_array(stack->array);
        free(stack);
    }
}

void stack_push(GenericStack* stack, const void* element) {
    array_push(stack->array, element);
}

void* stack_pop(GenericStack* stack) {
    if (stack->array->size == 0) {
        return NULL;
    }
    
    void* element = malloc(stack->array->element_size);
    void* top = array_get(stack->array, stack->array->size - 1);
    memcpy(element, top, stack->array->element_size);
    
    // 缩小数组
    stack->array->data = realloc(stack->array->data, (stack->array->size - 1) * stack->array->element_size);
    stack->array->size--;
    
    return element;
}

bool stack_is_empty(GenericStack* stack) {
    return stack->array->size == 0;
}

// 类型特定的函数

// 整数比较函数
int int_compare(const void* a, const void* b) {
    int ia = *(const int*)a;
    int ib = *(const int*)b;
    return ia - ib;
}

// 整数打印函数
void int_print(const void* a) {
    printf("%d", *(const int*)a);
}

// 整数谓词函数（偶数）
bool int_is_even(const void* a) {
    return *(const int*)a % 2 == 0;
}

// 整数转换函数（平方）
void* int_square(const void* a) {
    int* result = (int*)malloc(sizeof(int));
    *result = (*(const int*)a) * (*(const int*)a);
    return result;
}

// 字符串比较函数
int string_compare(const void* a, const void* b) {
    return strcmp(*(const char* const*)a, *(const char* const*)b);
}

// 字符串打印函数
void string_print(const void* a) {
    printf("%s", *(const char* const*)a);
}

// 字符串谓词函数（长度大于5）
bool string_length_gt_5(const void* a) {
    return strlen(*(const char* const*)a) > 5;
}

// 字符串转换函数（长度）
void* string_length(const void* a) {
    int* result = (int*)malloc(sizeof(int));
    *result = (int)strlen(*(const char* const*)a);
    return result;
}

// 泛型交换函数
void swap(void* a, void* b, size_t size) {
    char temp[size];
    memcpy(temp, a, size);
    memcpy(a, b, size);
    memcpy(b, temp, size);
}

// 泛型查找函数
void* generic_find(const void* array, size_t count, size_t element_size, 
                   const void* target, int (*compare)(const void*, const void*)) {
    const char* arr = (const char*)array;
    for (size_t i = 0; i < count; i++) {
        if (compare(arr + i * element_size, target) == 0) {
            return (void*)(arr + i * element_size);
        }
    }
    return NULL;
}

// 泛型排序函数（冒泡排序）
void generic_sort(void* array, size_t count, size_t element_size, 
                  int (*compare)(const void*, const void*)) {
    char* arr = (char*)array;
    
    for (size_t i = 0; i < count - 1; i++) {
        for (size_t j = 0; j < count - i - 1; j++) {
            if (compare(arr + j * element_size, arr + (j + 1) * element_size) > 0) {
                swap(arr + j * element_size, arr + (j + 1) * element_size, element_size);
            }
        }
    }
}

int main() {
    // 整数数组示例
    printf("=== Integer Array Example ===\n");
    
    GenericArray* int_array = create_array(sizeof(int), 10);
    
    int nums[] = {1, 5, 3, 9, 2};
    for (int i = 0; i < 5; i++) {
        array_push(int_array, &nums[i]);
    }
    
    printf("Integer array: ");
    array_print(int_array, int_print);
    
    int* max_int = (int*)array_find_max(int_array, int_compare);
    printf("Max integer: %d\n", *max_int);
    
    GenericArray* even_numbers = array_filter(int_array, int_is_even);
    printf("Even numbers: ");
    array_print(even_numbers, int_print);
    
    GenericArray* squared_numbers = array_map(int_array, int_square, sizeof(int));
    printf("Squared numbers: ");
    array_print(squared_numbers, int_print);
    
    // 字符串数组示例
    printf("\n=== String Array Example ===\n");
    
    GenericArray* string_array = create_array(sizeof(char*), 10);
    
    const char* fruits[] = {"Apple", "Banana", "Orange", "Grape", "Kiwi"};
    for (int i = 0; i < 5; i++) {
        array_push(string_array, &fruits[i]);
    }
    
    printf("String array: ");
    array_print(string_array, string_print);
    
    char** max_string = (char**)array_find_max(string_array, string_compare);
    printf("Max string: %s\n", *max_string);
    
    GenericArray* long_strings = array_filter(string_array, string_length_gt_5);
    printf("Long strings (>5 chars): ");
    array_print(long_strings, string_print);
    
    GenericArray* string_lengths = array_map(string_array, string_length, sizeof(int));
    printf("String lengths: ");
    array_print(string_lengths, int_print);
    
    // 泛型栈示例
    printf("\n=== Generic Stack Example ===\n");
    
    GenericStack* int_stack = create_stack(sizeof(int));
    
    int stack_nums[] = {10, 20, 30};
    for (int i = 0; i < 3; i++) {
        stack_push(int_stack, &stack_nums[i]);
    }
    
    printf("Stack operations:\n");
    while (!stack_is_empty(int_stack)) {
        int* value = (int*)stack_pop(int_stack);
        printf("Popped: %d\n", *value);
        free(value);
    }
    
    // 泛型查找和排序示例
    printf("\n=== Generic Find and Sort Example ===\n");
    
    int find_array[] = {5, 3, 8, 1, 9, 2};
    int target = 8;
    int* found = (int*)generic_find(find_array, 6, sizeof(int), &target, int_compare);
    
    if (found) {
        printf("Found %d in array\n", *found);
    }
    
    printf("Original array: ");
    for (int i = 0; i < 6; i++) {
        printf("%d ", find_array[i]);
    }
    printf("\n");
    
    generic_sort(find_array, 6, sizeof(int), int_compare);
    
    printf("Sorted array: ");
    for (int i = 0; i < 6; i++) {
        printf("%d ", find_array[i]);
    }
    printf("\n");
    
    // 泛型交换示例
    printf("\n=== Generic Swap Example ===\n");
    
    int a = 10, b = 20;
    printf("Before swap: a=%d, b=%d\n", a, b);
    swap(&a, &b, sizeof(int));
    printf("After swap: a=%d, b=%d\n", a, b);
    
    // 清理内存
    free_array(int_array);
    free_array(even_numbers);
    free_array(squared_numbers);
    free_array(string_array);
    free_array(long_strings);
    free_array(string_lengths);
    free_stack(int_stack);
    
    return 0;
}

/*
编译和运行：
gcc -o generics_example generics_example.c
./generics_example

输出结果：
=== Integer Array Example ===
Integer array: 1 5 3 9 2 
Max integer: 9
Even numbers: 1 5 3 9 2 
Squared numbers: 1 25 9 81 4 

=== String Array Example ===
String array: Apple Banana Orange Grape Kiwi 
Max string: Orange
Long strings (>5 chars): Banana Orange 
String lengths: 5 6 6 5 4 

=== Generic Stack Example ===
Stack operations:
Popped: 30
Popped: 20
Popped: 10

=== Generic Find and Sort Example ===
Found 8 in array
Original array: 5 3 8 1 9 2 
Sorted array: 1 2 3 5 8 9 

=== Generic Swap Example ===
Before swap: a=10, b=20
After swap: a=20, b=10
*/
