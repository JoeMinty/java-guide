# Lambda 表达式
## Lambda 管中窥豹
可以把**Lambda表达式**理解为一种简洁的可传递匿名函数：它没有名称，但它有参数列表，参数列表，返回类型，可能还有一个可以抛出的异常列表

## 在哪里以及如何使用Lambda
```java
List<Apple> greenApples = filter(inventory, (Apple a) -> GREEN.equals(a.getColor());
```

### 函数式接口
**函数式接口**就是定义一个抽象方法的接口，接口中还可以拥有**默认方法**，只要接口中只定义了一个**抽象方法**，它就仍然是一个函数式接口。

`Lambda`表达式允许直接以内联的形式为函数式接口的抽象方法提供实现，并把整个表达式作为函数式接口的实例

### 函数描述符
函数式接口的抽象方法的签名基本上就是`Lambda`表达式的签名，这种抽象方法叫做**函数描述符**

`@FunctionalInterface`用来修饰函数式接口

## 把Lambda付诸实践：环绕执行模式
- 行为参数化
- 使用函数式接口来传递行为
- 执行一个行为
- 传递`lambda`

## 使用函数式接口
函数式接口的抽象方法的签名称为**函数描述符**

### Predicate
`java.util.function.Predicate<T>`接口定义了一个名叫`test`的抽象方法，它接受泛型`T`对象，并返回一个`boolean`。

```java
@FunctionalInterface
public interface Predicate<T> {
  boolean test(T t);
}

public <T> List<T> filter(List<T> list, Predicate<T> p) {
  List<T> results = new ArrayList<>();
  for (T t : list) {
    if (p.test(t)) {
      results.add(t);
    }
  }
  return results;
}

Predicate<String> p = (String s) -> !s.isEmpty();
List<String> nonEmpty = filter(listOfStrings, p);
```

### Consumer
`java.util.function.Consumer<T>`接口定义了一个名叫`accept`的抽象方法，它接受泛型`T`的对象，没有返回（`void`）

```java
@FunctionalInterface
public interface Consumer<T> {
  void accept(T t);
}

public <T> void forEach(List<T> list, Consumer<T> c) {
  for (T t : list) {
    c.accept(t);
  }
}

forEach(Arrays.asList(1, 2, 3, 4, 5), (Integer i) -> System.out.println(i));
```

### Function
`java.util.function.Function<T, R>`接口定义了一个名叫`apply`的抽象方法，它接受泛型`T`的对象，并返回一个泛型`R`对象

```java
@FunctionalInterface 
public interface Function<T, R> {
  R apply(T t);
}

public <T, R> list<R> map(List<T> list, Function<T, R> f) {
  List<R> result = new ArrayList<>();
  for (T t : list) {
    result.add(f.apply(t));
  }
  return result;
}

List<Integer> l = map(Arrays.asList("hello", "world", "joe"), (String s) -> s.length());
```
