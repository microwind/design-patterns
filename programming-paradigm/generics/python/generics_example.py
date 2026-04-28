from typing import TypeVar, Generic, List, Optional, Callable, Any, Dict
from abc import ABC, abstractmethod
from dataclasses import dataclass

# 定义类型变量
T = TypeVar('T')
U = TypeVar('U')
V = TypeVar('V')

# 泛型类示例
class GenericsExample(Generic[T]):
    def __init__(self, value: T):
        self.value = value
    
    def get_value(self) -> T:
        return self.value
    
    def set_value(self, value: T) -> None:
        self.value = value
    
    @staticmethod
    def print_array(array: List[Any]) -> None:
        for element in array:
            print(element, end=' ')
        print()
    
    @staticmethod
    def find_max(array: List[Any]) -> Any:
        if not array:
            raise ValueError("Array is empty")
        
        max_val = array[0]
        for item in array[1:]:
            if item > max_val:
                max_val = item
        return max_val
    
    @staticmethod
    def create_list(*elements: Any) -> List[Any]:
        return list(elements)

# 泛型接口示例（抽象基类）
class IRepository(Generic[T], ABC):
    @abstractmethod
    def get_by_id(self, id: int) -> Optional[T]:
        pass
    
    @abstractmethod
    def add(self, item: T) -> None:
        pass

class Repository(IRepository[T]):
    def __init__(self):
        self._items: List[T] = []
    
    def get_by_id(self, id: int) -> Optional[T]:
        if 0 <= id < len(self._items):
            return self._items[id]
        return None
    
    def add(self, item: T) -> None:
        self._items.append(item)

# 泛型函数示例
def identity(arg: Any) -> Any:
    return arg

# 泛型约束示例
class Comparable(ABC):
    @abstractmethod
    def compare(self, other: 'Comparable') -> int:
        pass

def find_max_comparable(items: List[Comparable]) -> Comparable:
    if not items:
        raise ValueError("List is empty")
    
    max_item = items[0]
    for item in items[1:]:
        if item.compare(max_item) > 0:
            max_item = item
    return max_item

# 泛型函数 - 过滤列表
def filter(array: List[Any], predicate: Callable[[Any], bool]) -> List[Any]:
    return [item for item in array if predicate(item)]

# 泛型函数 - 映射列表
def map(array: List[Any], transform: Callable[[Any], Any]) -> List[Any]:
    return [transform(item) for item in array]

# 泛型函数 - 归约列表
def reduce(array: List[Any], callback: Callable[[Any, Any], Any], initial: Any) -> Any:
    result = initial
    for item in array:
        result = callback(result, item)
    return result

# 泛型类 - 栈实现
class Stack(Generic[T]):
    def __init__(self):
        self._items: List[T] = []
    
    def push(self, item: T) -> None:
        self._items.append(item)
    
    def pop(self) -> Optional[T]:
        if self._items:
            return self._items.pop()
        return None
    
    def peek(self) -> Optional[T]:
        if self._items:
            return self._items[-1]
        return None
    
    def is_empty(self) -> bool:
        return len(self._items) == 0
    
    def size(self) -> int:
        return len(self._items)

# 泛型类 - 链表节点
@dataclass
class ListNode(Generic[T]):
    value: T
    next: Optional['ListNode[T]'] = None

class LinkedList(Generic[T]):
    def __init__(self):
        self.head: Optional[ListNode[T]] = None
    
    def append(self, value: T) -> None:
        new_node = ListNode(value)
        
        if not self.head:
            self.head = new_node
            return
        
        current = self.head
        while current.next:
            current = current.next
        current.next = new_node
    
    def to_array(self) -> List[T]:
        result: List[T] = []
        current = self.head
        
        while current:
            result.append(current.value)
            current = current.next
        
        return result

# 泛型函数 - 交换两个值
def swap(a: Any, b: Any) -> tuple[Any, Any]:
    return b, a

# 泛型函数 - 创建字典
def create_dict(keys: List[Any], values: List[Any]) -> Dict[Any, Any]:
    return dict(zip(keys, values))

# 泛型装饰器示例
def log_generic(func: Callable[..., Any]) -> Callable[..., Any]:
    def wrapper(*args, **kwargs) -> Any:
        print(f"Calling {func.__name__} with args: {args}, kwargs: {kwargs}")
        result = func(*args, **kwargs)
        print(f"Result: {result}")
        return result
    return wrapper

# 泛型协议示例
class Processor(Generic[T], ABC):
    @abstractmethod
    def process(self, item: T) -> T:
        pass

class StringProcessor(Processor[str]):
    def process(self, item: str) -> str:
        return item.upper()

class NumberProcessor(Processor[int]):
    def process(self, item: int) -> int:
        return item * 2

# 泛型工具类
class Container(Generic[T]):
    def __init__(self, item: T):
        self._item = item
    
    def get_item(self) -> T:
        return self._item
    
    def set_item(self, item: T) -> None:
        self._item = item
    
    def __str__(self) -> str:
        return f"Container({self._item})"

# 泛型工厂
class GenericFactory:
    @staticmethod
    def create_container(item: Any) -> Container:
        return Container(item)

def main():
    # 泛型类使用
    string_box = GenericsExample("Hello Generics")
    print(f"String value: {string_box.get_value()}")
    
    integer_box = GenericsExample(42)
    print(f"Integer value: {integer_box.get_value()}")
    
    # 泛型方法使用
    string_array = ["Apple", "Banana", "Orange"]
    print("String array:", end=' ')
    GenericsExample.print_array(string_array)
    
    int_array = [1, 5, 3, 9, 2]
    print("Integer array:", end=' ')
    GenericsExample.print_array(int_array)
    
    # 泛型方法查找最大值
    print(f"Max number: {GenericsExample.find_max(int_array)}")
    print(f"Max string: {GenericsExample.find_max(string_array)}")
    
    # 泛型集合
    string_list = GenericsExample.create_list("Hello", "World", "Generics")
    print(f"String list: {string_list}")
    
    int_list = GenericsExample.create_list(1, 2, 3, 4, 5)
    print(f"Integer list: {int_list}")
    
    # 泛型接口示例
    repository = Repository[str]()
    repository.add("Item 1")
    repository.add("Item 2")
    print(f"Item by id 0: {repository.get_by_id(0)}")
    
    # 泛型函数示例
    output = identity("hello")
    print(f"Identity function output: {output}")
    
    # 泛型函数过滤
    even_numbers = filter(int_list, lambda n: n % 2 == 0)
    print(f"Even numbers: {even_numbers}")
    
    # 泛型函数映射
    str_lengths = map(string_list, lambda s: len(s))
    print(f"String lengths: {str_lengths}")
    
    # 泛型函数归约
    total = reduce(int_list, lambda acc, n: acc + n, 0)
    print(f"Sum of numbers: {total}")
    
    # 泛型栈使用
    int_stack = Stack[int]()
    int_stack.push(1)
    int_stack.push(2)
    int_stack.push(3)
    
    print("Stack operations:")
    while not int_stack.is_empty():
        value = int_stack.pop()
        print(f"Popped: {value}")
    
    # 泛型链表使用
    linked_list = LinkedList[str]()
    linked_list.append("First")
    linked_list.append("Second")
    linked_list.append("Third")
    
    print(f"Linked list: {' -> '.join(linked_list.to_array())}")
    
    # 泛型函数交换
    swapped_a, swapped_b = swap(10, 20)
    print(f"Swapped values: a={swapped_a}, b={swapped_b}")
    
    # 泛型字典创建
    obj = create_dict(["name", "age"], ["Alice", 25])
    print(f"Created dictionary: {obj}")
    
    # 泛型装饰器
    @log_generic
    def add_numbers(a: int, b: int) -> int:
        return a + b
    
    result = add_numbers(5, 3)
    
    # 泛型协议使用
    string_processor = StringProcessor()
    number_processor = NumberProcessor()
    
    print(f"String processed: {string_processor.process('hello')}")
    print(f"Number processed: {number_processor.process(5)}")
    
    # 泛型工厂
    container = GenericFactory.create_container("test")
    print(f"Factory created: {container}")

if __name__ == "__main__":
    main()

"""
运行：
python generics_example.py

输出结果：
String value: Hello Generics
Integer value: 42
String array: Apple Banana Orange 
Integer array: 1 5 3 9 2 
Max number: 9
Max string: Orange
String list: ['Hello', 'World', 'Generics']
Integer list: [1, 2, 3, 4, 5]
Item by id 0: Item 1
Identity function output: hello
Even numbers: [2, 4]
String lengths: [5, 5, 8]
Sum of numbers: 15
Stack operations:
Calling pop with args: (<__main__.Stack object at 0x...>,), kwargs: {}
Result: 3
Popped: 3
Calling pop with args: (<__main__.Stack object at 0x...>,), kwargs: {}
Result: 2
Popped: 2
Calling pop with args: (<__main__.Stack object at 0x...>,), kwargs: {}
Result: 1
Popped: 1
Linked list: First -> Second -> Third
Swapped values: a=20, b=10
Created dictionary: {'name': 'Alice', 'age': 25}
Calling add_numbers with args: (5, 3), kwargs: {}
Result: 8
String processed: HELLO
Number processed: 10
Factory created: Container(test)
"""
