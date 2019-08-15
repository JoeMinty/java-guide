# AQS（AbstractQueueSynchronizer）同步器
是用来构建锁或者其他同步组件的基础框架。

### 同步队列
同步器依赖内部的同步队列（一个FIFO双向队列）来完成同步状态的管理，当前线程获取同步状态失败时，同步器会将当前线程以及等待状态等信息构造成为一个节（Node）并将其加入同步队列，同时会阻塞当前线程，当同步状态释放时，会把首节点中的线程唤醒，使其再次尝试获取同步状态。同步队列中的节点（Node）用来保存获取同步状态失败的线程引用、等待状态以及前驱和后继节点。

节点是构成同步队列（等待队列）的基础，同步器拥有首节点（head）和尾节点（tail），没有成功获取同步状态的线程将会成为节点加入该队列的尾部。

### 独占式同步状态获取与释放
在获取同步状态时，同步器维护一个同步队列，获取状态失败的线程都会被加入到队列中并在队列中进行自旋；移出队列（或停止自旋）的条件是前驱节点为头节点且成功获取了同步状态。在释放同步状态时，同步器调用tryRelease(int arg)方法释放同步状态，然后唤醒头节点的后继节点。

### 共享式同步状态获取与释放
共享式获取与独占式获取最主要的区别在于同一时刻能否有多个线程同时获取到同步状态。

### 超时获取同步状态

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
