java并发编程指南 https://blog.csdn.net/qq_34337272/column/info/20860 
 
死磕系列： http://cmsblogs.com/?p=2611 
 
面试题系列： https://blog.csdn.net/linzhiqiang0316/article/details/80473906 
 
简书： https://www.jianshu.com/nb/4893857 
 
以上几个博客足够了，着重推荐一下死磕系列和简书的文章，比较深入

synchronized： https://blog.csdn.net/javazejian/article/details/72828483

多线程知识点

Semaphore信号量
CyclicBarrier循环屏障
CopyOnWriteArrayList--写复制列表(通过“副本”解决并发问题)

ReentrantLock(排他锁)，具有完全互斥排他的效果，即同一时刻只允许一个线程访问，影响效率。

ReadWriteLock接口的实现类-ReentrantReadWriteLock读写锁，读写锁维护了两个锁，一个是读操作相关的锁也成为共享锁，一个是写操作相关的锁 也称为排他锁。通过分离读锁和写锁，其并发性比一般排他锁有了很大提升。

多个读锁之间不互斥，读锁与写锁互斥，写锁与写锁互斥（只要出现写操作的过程就是互斥的。）。在没有线程Thread进行写入操作时，进行读取操作的多个Thread都可以获取读锁，而进行写入操作的Thread只有在获取写锁后才能进行写入操作。即多个Thread可以同时进行读取操作，但是同一时刻只允许一个Thread进行写入操作。
