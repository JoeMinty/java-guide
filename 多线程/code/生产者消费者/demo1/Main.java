public class Main {
  public static void main(String[] args) throws InterruptedException{
    BoundedQueue<String> queue = new BoundedQueue<>(10);
    P p = new P(queue);
    p.start();

//    Thread.sleep(2000);

    C c = new C(queue);
    c.start();
  }
}
