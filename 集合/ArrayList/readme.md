### ArrayList 简介
+ ArrayList 继承了**AbstractList**，实现了**List**。它是一个数组队列，提供了相关的添加、删除、修改、遍历等功能。
+ ArrayList 实现了**RandomAccess 接口**， RandomAccess 是一个标志接口，表明实现这个这个接口的 List 集合是支持快速随机访问的。在 ArrayList 中，我们即可以通过元素的序号快速获取元素对象，这就是快速随机访问。
+ ArrayList 实现了**Cloneable 接口**，即覆盖了函数 clone()，能被克隆。
+ ArrayList 实现**java.io.Serializable 接口**，这意味着ArrayList支持序列化，能通过序列化去传输。
+ 和 Vector 不同，ArrayList 中的操作不是线程安全的！所以，建议在单线程中才使用 ArrayList，而在多线程中可以选择 Vector 或者 **CopyOnWriteArrayList**。

