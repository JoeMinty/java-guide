# AQS（AbstractQueuedSynchronizer）同步器
是用来构建锁或者其他同步组件的基础框架。

同步器的主要使用方式是继承，子类通过继承同步器并实现它的抽象方法来管理同步状态，在抽象方法的实现过程中需要对同步状态进行更改

```java
getState() 获取当前同步状态

setState() 设置当前同步状态

compareAndSetState() 使用cas设置当前状态，该方法能够保证状态设置的原子性
```

同步器既可以支持独占式地获取同步状态，也可以支持共享式地获取同步状态

```
// 同步器可重写的方法

tryAcquire()

tryRelease()

tryAcquireShared()

tryReleaseShared()

isHeldExcusively() 当前同步器是否在独占模式下呗线程占用，一般该方法表示是否被当前线程所独占
``` 

```java

// 模板方法
acquire()

acquireInterruptibly()

tryAcquireNanos()

acquireShared()

acquireSharedInterruptibly()


```

### 同步队列
同步器依赖内部的同步队列（一个FIFO双向队列）来完成同步状态的管理，当前线程获取同步状态失败时，同步器会将当前线程以及等待状态等信息构造成为一个节（Node）并将其加入同步队列，同时会阻塞当前线程，当同步状态释放时，会把首节点中的线程唤醒，使其再次尝试获取同步状态。同步队列中的节点（Node）用来保存获取同步状态失败的线程引用、等待状态以及前驱和后继节点。

节点是构成同步队列（等待队列）的基础，同步器拥有首节点（head）和尾节点（tail），没有成功获取同步状态的线程将会成为节点加入该队列的尾部。

### 独占式同步状态获取与释放
在获取同步状态时，同步器维护一个同步队列，获取状态失败的线程都会被加入到队列中并在队列中进行自旋；移出队列（或停止自旋）的条件是前驱节点为头节点且成功获取了同步状态。在释放同步状态时，同步器调用tryRelease(int arg)方法释放同步状态，然后唤醒头节点的后继节点。

### 共享式同步状态获取与释放
共享式获取与独占式获取最主要的区别在于同一时刻能否有多个线程同时获取到同步状态。

### 超时获取同步状态
超时获取同步状态过程可以被视作响应中断获取同步状态过程的“增强版”，doAcquireNanos(int arg,long nanosTimeout)方法在支持响应中断的基础上，增加了超时获取的特性。针对超时获取，主要需要计算出需要睡眠的时间间隔nanosTimeout，为了防止过早通知，nanosTimeout计算公式为：nanosTimeout-=now-lastTime，其中now为当前唤醒时间，lastTime为上次唤醒时间，如果nanosTimeout大于0则表示超时时间未到，需要继续睡眠nanosTimeout纳秒，反之，表示已经超时

```java
private boolean doAcquireNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (nanosTimeout <= 0L)
            return false;
        final long deadline = System.nanoTime() + nanosTimeout;
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return true;
                }
                nanosTimeout = deadline - System.nanoTime();
                if (nanosTimeout <= 0L)
                    return false;
                if (shouldParkAfterFailedAcquire(p, node) &&
                    nanosTimeout > spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if (Thread.interrupted())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
```


### 自定义aqs

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TwinsLock implements Lock {

  private final Sync sync = new Sync(2);

  private static final class Sync extends AbstractQueuedSynchronizer {

    Sync(int count) {
      if (count <= 0) {
        throw new IllegalArgumentException("count must large than zero.");
      }

      setState(count);
    }

    public int tryAcquireShared(int reduceCount) {
      for (;;) {
        int current = getState();
        int newCount = current - reduceCount;
        if (newCount < 0 || compareAndSetState(current, newCount)) {
          return newCount;
        }
      }
    }

    public boolean tryReleaseShared(int returnCount) {
      for (;;) {
        int current = getState();
        int newCount = current + returnCount;
        if (compareAndSetState(current, newCount)) {
          return true;
        }
      }
    }

  }

  @Override
  public void lock() {
    sync.tryAcquireShared(1);
  }

  @Override
  public void unlock() {
    sync.releaseShared(1);
  }

  @Override
  public void lockInterruptibly() throws InterruptedException {

  }

  @Override
  public boolean tryLock() {
    return false;
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    return false;
  }

  @Override
  public Condition newCondition() {
    return null;
  }
}


```

### 读写锁

分析`ReentrantReadWriteLock`的实现

- 读写状态的设计

读写锁状态变量切分成两个部分，高16位表示读，低16位表示写

读状态 S >>> 16

写状态 S & 0x0000FFFF

读状态+1  S + (1 << 16) => S + 0x00010000

写状态+1  S + 1

- 写锁的获取与释放

如果当前线程已经获取了写锁，则增加写状态。如果当前线程在获取写锁时，读锁已经被获取（读状态不为0）或者该线程不是已经获取写锁的线程，则当前线程进入等待状态

```java
protected final boolean tryAcquire(int acquires) {
            /*
             * Walkthrough:
             * 1. If read count nonzero or write count nonzero
             *    and owner is a different thread, fail.
             * 2. If count would saturate, fail. (This can only
             *    happen if count is already nonzero.)
             * 3. Otherwise, this thread is eligible for lock if
             *    it is either a reentrant acquire or
             *    queue policy allows it. If so, update state
             *    and set owner.
             */
            Thread current = Thread.currentThread();
            int c = getState();
            int w = exclusiveCount(c);
            if (c != 0) {
                // (Note: if c != 0 and w == 0 then shared count != 0)
                if (w == 0 || current != getExclusiveOwnerThread())
                    return false;
                if (w + exclusiveCount(acquires) > MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                // Reentrant acquire
                setState(c + acquires);
                return true;
            }
            if (writerShouldBlock() ||
                !compareAndSetState(c, c + acquires))
                return false;
            setExclusiveOwnerThread(current);
            return true;
        }

```
 

- 读锁的获取与释放

读锁是一个支持可重入的共享锁，能够被多个线程同时获取，在没有其他写线程访问（或者写状态为0）时，读锁总会被成功获取，如果当前线程已经获取了读锁，则增加读状态。如果当前线程在获取读锁时，写锁已经被其他线程获取，则进入等待状态。

- 锁降级

```java
class CachedData {
    Object data;
    volatile boolean cacheValid;
    // 读写锁实例
    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    void processCachedData() {
        // 获取读锁
        rwl.readLock().lock();
        if (!cacheValid) { // 如果缓存过期了，或者为 null
            // 释放掉读锁，然后获取写锁 (后面会看到，没释放掉读锁就获取写锁，会发生死锁情况)
            rwl.readLock().unlock();
            rwl.writeLock().lock();

            try {
                if (!cacheValid) { // 重新判断，因为在等待写锁的过程中，可能前面有其他写线程执行过了
                    data = ...
                    cacheValid = true;
                }
                // 获取读锁 (持有写锁的情况下，是允许获取读锁的，称为 “锁降级”，反之不行。)
                rwl.readLock().lock();
            } finally {
                // 释放写锁，此时还剩一个读锁
                rwl.writeLock().unlock(); // Unlock write, still hold read
            }
        }

        try {
            use(data);
        } finally {
            // 释放读锁
            rwl.readLock().unlock();
        }
    }
}

```


### LockSupport
LockSupport定义了一组的公共静态方法，这些方法提供了最基本的线程阻塞和唤醒功能，而LockSupport也成为构建同步组件的基础工具

### Condition
`Codition`对象依赖`Lock`对象,。当调用`await()`方法后，当前线程会释放锁并在此等待，而其他线程调用`Condition`对象的`signal()`方法，通知当前线程后，当前线程才从`await()`方法返回，并且在返回前已经获取了锁。

```java
  
  Lock lock = new ReentrantLock();
  Condition condition = lock.newCondition();
  
  public void conditionWait() throw InterruptedException {
    lock.lock();
    try {
      condition.await();
    } finally {
      lock.unlock();
    }
  }
  
  public void conditionSignal() throw InterruptedException {
    lock.lock();
    try {
      condition.signal();
    } finally {
      lock.unlock();
    }
  }

```
