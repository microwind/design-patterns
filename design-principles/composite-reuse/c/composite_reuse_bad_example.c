#include <stdio.h>
/**
 * 组合复用原则 - 反例
 * 这个例子违反了组合复用原则，因为采用了继承而不是组合。
 * 1. 人的身份会有多重角色，不适合用继承表达；继承更适合物种层级。
 * 2. 采用继承不够灵活，修改很麻烦，例如一个人既是经理也是雇员。
 * 对比：正例通过组合/聚合复用角色，让对象关系更灵活可扩展。
 */

typedef struct {
  const char *name;
  int age;
} Person;

// 违规点：用层级结构模拟继承，角色组合不灵活
typedef struct {
  Person person;
  int id;
  const char *title;
} Employee;

// 角色变化会迫使类型层级不断扩展
typedef struct {
  Employee employee;
} Engineer;

typedef struct {
  Employee employee;
} Manager;

void engineer_work(Engineer *engineer) {
  printf(
    "Engineer is working. id = %d, title = %s name = %s, age = %d\n",
    engineer->employee.id,
    engineer->employee.title,
    engineer->employee.person.name,
    engineer->employee.person.age
  );
}

void manager_work(Manager *manager) {
  printf(
    "Manager is working. id = %d, title = %s name = %s, age = %d\n",
    manager->employee.id,
    manager->employee.title,
    manager->employee.person.name,
    manager->employee.person.age
  );
}

int main() {
  Engineer engineer = { { {"Tom", 25}, 1001, "senior engineer" } };
  Manager manager = { { {"Jerry", 45}, 2002, "advanced director" } };
  engineer_work(&engineer);
  manager_work(&manager);
  return 0;
}
/**
 * 合成复用原则 - 反例
 * 这个例子违反了合成复用原则，因为采用了继承而不是组合。
 * 1. 人的身份会有多重角色，不适合用继承表达；继承更适合物种层级。
 * 2. 采用继承不够灵活，修改很麻烦，例如一个人既是经理也是雇员。
 * 对比：正例通过组合/聚合复用角色，让对象关系更灵活可扩展。
 */
