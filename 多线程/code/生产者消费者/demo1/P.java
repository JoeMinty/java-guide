/**
 * 生产者
 */
public class P extends Thread {

  private BoundedQueue<String> queue;

  P(BoundedQueue<String> queue) {
    this.queue = queue;
  }

  @Override
  public void run() {
    while (true) {
      try {
        queue.add(System.currentTimeMillis()+"");
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }
  }
}
