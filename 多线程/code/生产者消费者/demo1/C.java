
/**
 * 消费者
 */
public class C extends Thread {

  private BoundedQueue<String> queue;

  C(BoundedQueue<String> queue) {
    this.queue = queue;
  }

  @Override
  public void run() {
    while (true) {
      try {
        System.out.println(queue.remove());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }
  }
}
