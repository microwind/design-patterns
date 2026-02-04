/**
 * 通过组合来替代继承进行组合/聚合复用，将有关联的类聚合到业务类中。
 * 组合，相对紧密，表示为组成部件与物体的关系
 * 聚合，相对松散，表示为个体与整体/模块的关系
 */

// C语言没有类型继承或接口的概念，通过struct和函数指针来实现类似功能。
// 一个结构体（struct）包含一个或多个其他类型（可能是基本类型或其他struct）

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Person 结构体表示人物
typedef struct Person
{
  char *name;
  int age;
} Person;

// Person新建函数
Person *new_person(const char *name, int age)
{
  Person *p = malloc(sizeof(Person));
  p->name = strdup(name);
  p->age = age;
  return p;
}

void free_person(Person *p)
{
  free(p->name);
  free(p);
}

const char *get_name(Person *p)
{
  return p->name;
}

int get_age(Person *p)
{
  return p->age;
}

// Employee 结构体表示雇员
typedef struct Employee
{
  int id;
  char *title;
  // 聚合人物类，通过聚合方式更加松耦合，利于扩展
  Person *person;
  // 模拟 Work 行为的函数指针
  int (*work)(struct Employee *e);
} Employee;

// Employee 的 Work 方法实现
int employee_work(Employee *e)
{
  printf("Employee is working (default behavior).\n");
  return 1;
}

// 创建 Employee 结构体实例的函数
Employee *new_employee(int id, const char *title, Person *person)
{
  Employee *e = malloc(sizeof(Employee));
  e->id = id;
  e->title = strdup(title);
  e->person = person;
  e->work = employee_work; // 默认工作行为
  return e;
}

// Engineer 结构体通过包含 Employee 结构体指针来实现组合
typedef struct Engineer
{
  // 没法直接继承Employee，通过指针关联来达到组合目的
  Employee *employee;
} Engineer;

// Engineer 的 Work 方法实现
int engineer_work(Employee *e)
{
  printf("Engineer is working. id = %d, title = %s, name = %s, age = %d\n",
         e->id, e->title, get_name(e->person), get_age(e->person));
  return 1;
}

// 创建 Engineer 结构体实例的函数
Engineer *new_engineer(int id, const char *title, Person *person)
{
  Engineer *e = malloc(sizeof(Engineer));
  e->employee = new_employee(id, title, person);
  e->employee->work = (int (*)(Employee *))engineer_work; // 设置 Engineer 特有的工作行为
  return e;
}

// Manager 结构体通过包含 Employee 结构体指针来实现组合
typedef struct Manager
{
  // 没法直接继承Employee，通过指针关联来达到组合目的
  Employee *employee;
} Manager;

// Manager 的 Work 方法实现
int manager_work(Employee *e)
{
  printf("Manager is working. id = %d, title = %s, name = %s, age = %d\n",
         e->id, e->title, get_name(e->person), get_age(e->person));
  return 1;
}

// 创建 Manager 结构体实例的函数
Manager *create_manager(int id, const char *title, Person *person)
{
  Manager *m = malloc(sizeof(Manager));
  m->employee = new_employee(id, title, person);
  m->employee->work = (int (*)(Employee *))manager_work; // 设置 Manager 特有的工作行为
  return m;
}

// 测试验证
int main()
{
  Person *person = new_person("Tom", 25);
  Engineer *engineer = new_engineer(1001, "senior engineer", person);
  engineer->employee->work(engineer->employee); // 调用 Engineer 的 Work 方法

  Manager *manager = create_manager(2002, "advanced director", new_person("Jerry", 45));
  manager->employee->work(manager->employee); // 调用 Manager 的 Work 方法

  // 释放内存
  free_person(person);
  free_person(manager->employee->person);
  free(engineer->employee->title);
  free(manager->employee->title);
  free(engineer->employee);
  free(manager->employee);
  free(engineer);
  free(manager);

  return 0;
}

/**
jarry@jarrys-MBP c % gcc composite_reuse_good_example.c 
jarry@jarrys-MBP c % ./a.out 
Engineer is working. id = 1001, title = senior engineer, name = Tom, age = 25
Manager is working. id = 2002, title = advanced director, name = Jerry, age = 45
*/
