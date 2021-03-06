## 概述
每秒事务处理数（Transactions Per Second， TPS）是衡量一个服务性能高低好坏的重要指标之一，代表着一秒内服务端平均能响应的请求总数。

## Java内存模型
JMM（Java Memory Model）实现java程序在各平台下都能达到一致的内存访问效果，屏蔽掉各种硬件和操作系统的内存访问差异。

Java线程之间的通信由Java内存模型控制，JMM决定一个线程对共享变量的写入何时对领一个线程可见。JMM是通过控制主内存与每个线程的本地内存之间的交互，来为Java程序提供内存可见性保证。

JMM属于语言级的内存模型，它确保在不同的编译器和不同的处理器平台之上，通过禁止特定类型的编译器重排序和处理器重排序，达到一致的内存可见性保证。

### 重排序
- 编译器优化的重排序（编译器重排序）
- 指令集并行的重排序（处理器重排序）
- 内存系统的重排序（处理器重排序）

#### 数据依赖性
编译器和处理器在重排序时，会遵守数据依赖性，编译器和处理器不会改变存在数据依赖关系的两个操作的执行顺序

#### as-if-serial
不管怎么重排序（编译器和处理器为了提高并行度），（单线程）程序的执行结果不能被改变

### 主内存和工作内存
Java内存模型规定了所有的变量（包括了实例字段，静态字段和构成数组对象的元素）都存储在主内存（Main Memory）中。每条线程还有自己的工作内存，线程对变量的所有操作（读取、赋值等）都必须在工作内存中进行，而不能直接读写主内存中的变量，线程间变量值的传递均需要通过主内存来完成。

### 内存间交互操作
- **lock**（锁定）：作用于主内存的变量，它把一个变量标识为一条线程独占的状态
- **unlock**（解锁）：作用于主内存的变量，它把一个处于锁定状态的变量释放出来，释放后的变量才可以被其他线程锁定
- **read**（读取）：作用于主内存的变量，它把一个变量的值从主内存传输到线程的工作内存中，以便随后的load动作使用
- **load**（载入）：作用于工作内存的变量，它把read操作从主内存中得到的变量值放入工作内存的变量副本中
- **use**（使用）：作用于工作内存的变量，它把工作内存中一个变量的值传递给执行引擎，每当虚拟机遇到一个需要使用到变量的值的字节码指令时将会执行这个操作
- **assign**（赋值）：作用于工作内存的变量，它把一个从执行引擎接收到的值赋给工作内存的变量，每当虚拟机遇到一个给变量赋值的字节码指令时执行这个操作
- **store**（存储）：作用于工作内存的变量，它把工作内存中一个变量的值传送到主内存中，以便随后的write操作使用
- **write**（写入）：作用于主内存的变量，它把store操作从工作内存中得到的变量的值放入主内存的变量中

### 原子性、可见性与有序性
**原子性**

基本数据类型的访问读写是具备原子性的，long和double非原子性

**可见性**

可见性是指当一个线程修改了共享变量的值，其他线程能够立即得知这个修改。
Java内存模型是通过在变量修改后将新值同步回主内存，在变量读取前从主内存刷新变量值，**volatile** 的特殊规则保证了新值能立即同步到主内存，以及每次使用前立即从主内存刷新。
除了 **volatile** 意外，**synchronized** 和 **final** 也能实现可见性。

**有序性**

**volatile** 和 **synchronized** 两个关键字来保证线程之间的有序性，**volatile** 关键字本身就包含了禁止指令重排序的语义，而 **synchronized** 则是由“一个变量在同一个时刻只允许一条线程对其进行lock操作”这条规则获得的，这条规则决定了持有同一个锁的两个同步块只能串行进行。

### 先行发生原则（happens-before）
先行发生是指Java内存模型中定义的两项操作之间的偏序关系。

- 程序次序规则：在一个线程内，书写在前面的操作先行发生于写在后面的操作

- 监视器锁定规则：一个unlock操作先行发生于后面对同一个锁的lock操作

- volatile变量规则：对于同一个volatile变量的写操作必然先行发生于后面对这个变量的读操作

- 线程启动规则：Thread对象的start方法先行发生于此线程的每一个动作

- 线程终止规则：线程中的所有操作都先行发生于对此线程的终止检测

- 线程中断规则：对线程interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生

- 对象终结规则：一个对象的初始化完成（构造函数执行结束）先行发生于它的finalize()方法的开始

- 传递性：如果操作A先行发生于操作B，操作B先行发生于操作C，那么操作A先行发生于操作C

### 顺序一致性内存模型
两大特性：

- 一个线程中所有操作必须按照程序的顺序来执行

- 不管程序是否同步，所有程序都只能看到一个单一的操作执行顺序，在顺序一致性内存模型中，每个操作都必须原子执行且立刻对所有线程可见
