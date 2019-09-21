# Lock
## Lock接口

```java
void lock() 获取锁

void lockInterruptibly() 可中断地获取锁

void tryLock() 尝试非阻塞的获取锁，调用该方法后立刻返回

Condition newCondition() 获取等待通知组件，该组件和当前的锁绑定，当前线程只有获得了锁，才能调用该组件的wait()方法，而调用后，当前线程将释放锁

```


锁实现提供比使用同步方法获得的更广泛的锁操作和陈述。它们允许更灵活的结构，可能具有完全不同的属性，并且可以支持多个关联的Condition对象。

### 使用Condition实现等待/通知

**synchronized**与`wait()`和`notify()`/`notifyAll()`方法结合可以实现等待/通知模式

一个`Lock`对象可以创建多个`Condition`（对象监视器）实例，线程对象可以注册在指定的`Condition`中，从而可以选择性进行线程通知，在调度线程上更加灵活。
