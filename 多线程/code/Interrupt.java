public class Interrupt {

  public static void main(String[] args) throws Exception {
    //    Worker t = new Worker();
    //    t.start();
    //
    //    Thread.sleep(2000);
    //    System.out.println(t.isInterrupted());
    //    System.out.println(t.isInterrupted());
    //    t.interrupt();
    //
    //    System.out.println(t.isInterrupted());
    //    System.out.println("Main stop");
    Thread thread =
        new Thread(
            new Runnable() {

              @Override
              public void run()  {
                // TODO Auto-generated method stub
                System.out.println("ffff==="+ Thread.currentThread().isInterrupted());
                while (true) {
                  try{
                    Thread.sleep(1);
                    System.out.println("ccc==="+ Thread.currentThread().isInterrupted());
                    if (Thread.interrupted()) {
                      System.out.println("ddd==="+ Thread.currentThread().isInterrupted());
                      System.err.println("线程中断标志位为true");
                      return;
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }

                }
              }
            });
    System.out.println("aaaa==" + thread.isInterrupted());
    thread.start();
    Thread.sleep(2);
    thread.interrupt();
    Thread.sleep(2000);
    System.out.println("bbbb==" + thread.isInterrupted());
  }


  public static class Worker extends Thread {
    @Override
    public void run() {
      System.out.println("worker started");

      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        System.out.println("the thread is === " + Thread.currentThread().getName());
        System.out.println("Worker IsInterrupted: " + Thread.currentThread().isInterrupted());
      }

      System.out.println("Worker stopped");
    }
  }

}
