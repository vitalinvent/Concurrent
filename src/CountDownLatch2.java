import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CountDownLatch2 {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(3);
        Runnable r = () -> {
            try {
                Thread.sleep(1000);
                System.out.println("Service in " + Thread.currentThread().getName() + " initialized.");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        executor.execute(r);
        executor.execute(r);
        executor.execute(r);
        try {
            latch.await(2, TimeUnit.SECONDS);
            System.out.println("All services up and running!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}
