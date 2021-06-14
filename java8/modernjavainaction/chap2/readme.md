# 通过行为参数化传递代码
**行为参数化**可以帮助处理频繁变更的需求的一种软件开发模式

## 行为参数化
**谓语**即一个返回`boolean`值的函数，需要定义一个接口来对**选择标准建模**

```java
public interface ApplePredicate {
  boolean test(Apple apple);
}

// 可以用ApplePredicate的多个实现代表不同的选择标准
public class AppleHeavyWeightPredicate implements ApplePredicate {
  public boolean test(Apple apple) {
    return apple.getWeight() > 150;
  }
}

public class AppleGreenPredicate implements ApplePredicate {
  public boolean test(Apple apple) {
    return apple.getColor().equals("green");
  }
}

public class AppleHeavyWeightPredicate implements ApplePredicate {
  public boolean test(Apple apple) {
    return apple.getWeight() > 150;
  }
}
```

上述类似**策略模式**，`ApplePredicate`就是算法族，实现类就是不同的策略。

## 总结
- 行为参数化就是一个方法**接受**多个不同的行为作为参数，并在内部使用它们，**完成**不同行为的能力
- 行为参数化可让代码更好地适应不断变化的要求，减轻未来的工作量
- 传递代码就是将新行为作为参数传递给方法，可使用匿名类
