// 泛型结构体示例
#[derive(Debug, Clone)]
pub struct GenericsExample<T> {
    value: T,
}

impl<T> GenericsExample<T> {
    // 泛型构造函数
    pub fn new(value: T) -> Self {
        GenericsExample { value }
    }
    
    // 泛型方法
    pub fn get_value(&self) -> &T {
        &self.value
    }
    
    pub fn set_value(&mut self, value: T) {
        self.value = value;
    }
}

// 独立的泛型函数 - 打印数组
pub fn print_array<E: std::fmt::Debug>(array: &[E]) {
    for element in array {
        print!("{:?} ", element);
    }
    println!();
}

// 独立的泛型函数 - 查找最大值
pub fn find_max<T: std::cmp::PartialOrd + Copy>(slice: &[T]) -> T {
    if slice.is_empty() {
        panic!("Array is empty");
    }
    
    let mut max = slice[0];
    for &item in slice.iter().skip(1) {
        if item > max {
            max = item;
        }
    }
    max
}

// 独立的泛型函数 - 创建向量
pub fn create_vec<T: Copy>(elements: &[T]) -> Vec<T> {
    elements.to_vec()
}

// 泛型trait示例
pub trait IRepository<T> {
    fn get_by_id(&self, id: usize) -> Option<&T>;
    fn add(&mut self, item: T);
    fn len(&self) -> usize;
}

pub struct Repository<T> {
    items: Vec<T>,
}

impl<T> Repository<T> {
    pub fn new() -> Self {
        Repository { items: Vec::new() }
    }
}

impl<T> IRepository<T> for Repository<T> {
    fn get_by_id(&self, id: usize) -> Option<&T> {
        self.items.get(id)
    }
    
    fn add(&mut self, item: T) {
        self.items.push(item);
    }
    
    fn len(&self) -> usize {
        self.items.len()
    }
}

// 泛型函数示例
pub fn identity<T>(arg: T) -> T {
    arg
}

// 泛型约束示例
pub trait Display {
    fn display(&self) -> String;
}

impl Display for String {
    fn display(&self) -> String {
        self.clone()
    }
}

impl Display for i32 {
    fn display(&self) -> String {
        self.to_string()
    }
}

pub fn display_item<T: Display>(item: &T) -> String {
    item.display()
}

// 泛型函数 - 过滤切片
pub fn filter<T, F>(slice: &[T], predicate: F) -> Vec<T>
where
    T: Clone,
    F: Fn(&T) -> bool,
{
    slice.iter().filter(|x| predicate(x)).cloned().collect()
}

// 泛型函数 - 映射切片
pub fn map<T, U, F>(slice: &[T], transform: F) -> Vec<U>
where
    F: Fn(&T) -> U,
{
    slice.iter().map(transform).collect()
}

// 泛型函数 - 归约切片
pub fn reduce<T, F>(slice: &[T], callback: F, initial: T) -> T
where
    F: Fn(T, &T) -> T,
    T: Clone,
{
    let mut result = initial;
    for item in slice {
        result = callback(result, item);
    }
    result
}

// 泛型结构体 - 栈实现
#[derive(Debug)]
pub struct Stack<T> {
    items: Vec<T>,
}

impl<T> Stack<T> {
    pub fn new() -> Self {
        Stack { items: Vec::new() }
    }
    
    pub fn push(&mut self, item: T) {
        self.items.push(item);
    }
    
    pub fn pop(&mut self) -> Option<T> {
        self.items.pop()
    }
    
    pub fn peek(&self) -> Option<&T> {
        self.items.last()
    }
    
    pub fn is_empty(&self) -> bool {
        self.items.is_empty()
    }
    
    pub fn len(&self) -> usize {
        self.items.len()
    }
}

// 泛型结构体 - 链表节点
#[derive(Debug)]
pub struct ListNode<T> {
    value: T,
    next: Option<Box<ListNode<T>>>,
}

impl<T> ListNode<T> {
    pub fn new(value: T) -> Self {
        ListNode { value, next: None }
    }
}

#[derive(Debug)]
pub struct LinkedList<T> {
    head: Option<Box<ListNode<T>>>,
}

impl<T> LinkedList<T> {
    pub fn new() -> Self {
        LinkedList { head: None }
    }
    
    pub fn append(&mut self, value: T) {
        let new_node = Box::new(ListNode::new(value));
        
        match self.head {
            None => {
                self.head = Some(new_node);
            }
            Some(ref mut current) => {
                let mut current = current;
                while let Some(ref mut next) = current.next {
                    current = next;
                }
                current.next = Some(new_node);
            }
        }
    }
    
    pub fn to_vec(&self) -> Vec<&T> {
        let mut result = Vec::new();
        let mut current = &self.head;
        
        while let Some(ref node) = current {
            result.push(&node.value);
            current = &node.next;
        }
        
        result
    }
}

// 泛型函数 - 交换两个值
pub fn swap<T>(a: T, b: T) -> (T, T) {
    (b, a)
}

// 泛型trait - 可比较
pub trait Comparable {
    fn compare(&self, other: &Self) -> std::cmp::Ordering;
}

impl Comparable for String {
    fn compare(&self, other: &Self) -> std::cmp::Ordering {
        self.cmp(other)
    }
}

impl Comparable for i32 {
    fn compare(&self, other: &Self) -> std::cmp::Ordering {
        self.cmp(other)
    }
}

// 泛型函数 - 查找最大值（使用自定义trait）
pub fn find_max_comparable<T: Comparable>(items: &[T]) -> Option<&T> {
    if items.is_empty() {
        return None;
    }
    
    let mut max = &items[0];
    for item in items.iter().skip(1) {
        if item.compare(max) == std::cmp::Ordering::Greater {
            max = item;
        }
    }
    Some(max)
}

// 泛型结构体 - 容器
#[derive(Debug)]
pub struct Container<T> {
    item: T,
}

impl<T> Container<T> {
    pub fn new(item: T) -> Self {
        Container { item }
    }
    
    pub fn get_item(&self) -> &T {
        &self.item
    }
    
    pub fn set_item(&mut self, item: T) {
        self.item = item;
    }
}

// 泛型工厂
pub struct GenericFactory;

impl GenericFactory {
    pub fn create_container<T>(item: T) -> Container<T> {
        Container::new(item)
    }
}

// 泛型trait - 处理器
pub trait Processor<T> {
    fn process(&self, item: T) -> T;
}

pub struct StringProcessor;

impl Processor<String> for StringProcessor {
    fn process(&self, item: String) -> String {
        item.to_uppercase()
    }
}

pub struct NumberProcessor;

impl Processor<i32> for NumberProcessor {
    fn process(&self, item: i32) -> i32 {
        item * 2
    }
}

fn main() {
    // 泛型结构体使用
    let string_box = GenericsExample::new("Hello Generics".to_string());
    println!("String value: {}", string_box.get_value());
    
    let integer_box = GenericsExample::new(42);
    println!("Integer value: {}", integer_box.get_value());
    
    // 泛型方法使用
    let string_array = ["Apple", "Banana", "Orange"];
    print!("String array: ");
    print_array(&string_array);
    
    let int_array = [1, 5, 3, 9, 2];
    print!("Integer array: ");
    print_array(&int_array);
    
    // 泛型方法查找最大值
    println!("Max number: {}", find_max(&int_array));
    println!("Max string: {}", find_max(&string_array));
    
    // 泛型集合
    let string_elements = ["Hello", "World", "Generics"];
    let string_list = create_vec(&string_elements);
    println!("String list: {:?}", string_list);
    
    let int_elements = [1, 2, 3, 4, 5];
    let int_list = create_vec(&int_elements);
    println!("Integer list: {:?}", int_list);
    
    // 泛型trait示例
    let mut repository = Repository::new();
    repository.add("Item 1".to_string());
    repository.add("Item 2".to_string());
    println!("Item by id 0: {:?}", repository.get_by_id(0));
    
    // 泛型函数示例
    let output = identity("hello");
    println!("Identity function output: {}", output);
    
    // 泛型约束示例
    let string_item = "Hello World".to_string();
    println!("Display string: {}", display_item(&string_item));
    
    let int_item = 42;
    println!("Display int: {}", display_item(&int_item));
    
    // 泛型函数过滤
    let even_numbers = filter(&int_list, |&n| n % 2 == 0);
    println!("Even numbers: {:?}", even_numbers);
    
    // 泛型函数映射切片
    let str_lengths: Vec<usize> = map(&string_list, |s: &&str| s.len());
    println!("String lengths: {:?}", str_lengths);
    
    // 泛型函数归约
    let total = reduce(&int_list, |acc, &n| acc + n, 0);
    println!("Sum of numbers: {}", total);
    
    // 泛型栈使用
    let mut int_stack = Stack::new();
    int_stack.push(1);
    int_stack.push(2);
    int_stack.push(3);
    
    println!("Stack operations:");
    while !int_stack.is_empty() {
        if let Some(value) = int_stack.pop() {
            println!("Popped: {}", value);
        }
    }
    
    // 泛型链表使用
    let mut linked_list = LinkedList::new();
    linked_list.append("First".to_string());
    linked_list.append("Second".to_string());
    linked_list.append("Third".to_string());
    
    println!("Linked list: {:?}", linked_list.to_vec());
    
    // 泛型函数交换
    let (swapped_a, swapped_b) = swap(10, 20);
    println!("Swapped values: a={}, b={}", swapped_a, swapped_b);
    
    // 泛型trait使用
    let strings = vec!["Apple".to_string(), "Banana".to_string(), "Orange".to_string()];
    if let Some(max_string) = find_max_comparable(&strings) {
        println!("Max string: {}", max_string);
    }
    
    let numbers = vec![1, 5, 3, 9, 2];
    if let Some(max_number) = find_max_comparable(&numbers) {
        println!("Max number: {}", max_number);
    }
    
    // 泛型容器
    let container = GenericFactory::create_container("test".to_string());
    println!("Factory created: {:?}", container);
    
    // 泛型处理器
    let string_processor = StringProcessor;
    let number_processor = NumberProcessor;
    
    println!("String processed: {}", string_processor.process("hello".to_string()));
    println!("Number processed: {}", number_processor.process(5));
}

/*
编译和运行：
rustc generics_example.rs
./generics_example

输出结果：
String value: Hello Generics
Integer value: 42
String array: "Apple" "Banana" "Orange" 
Integer array: 1 5 3 9 2 
Max number: 9
Max string: Orange
String list: ["Hello", "World", "Generics"]
Integer list: [1, 2, 3, 4, 5]
Item by id 0: Some("Item 1")
Identity function output: hello
Display string: Hello World
Display int: 42
Even numbers: [2, 4]
String lengths: [5, 5, 8]
Sum of numbers: 15
Stack operations:
Popped: 3
Popped: 2
Popped: 1
Linked list: ["First", "Second", "Third"]
Swapped values: a=20, b=10
Max string: Orange
Max number: 9
Factory created: Container { item: "test" }
String processed: HELLO
Number processed: 10
*/
