import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SynchronizersCyclicBarrier1 {

    public static void main(String[] args) {

//      Циклический барьер является точкой синхронизации, в которой указанное количество
//      параллельных потоков встречается и блокируется. Как только все потоки прибыли,
//      выполняется опционное действие (или не выполняется, если барьер был
//      инициализирован без него), и, после того, как оно выполнено, барьер ломается
//      и ожидающие потоки «освобождаются».
//      В конструктор барьера (CyclicBarrier(int parties) и CyclicBarrier(int parties,
//      Runnable barrierAction)) обязательно передается количество сторон,
//      которые должны «встретиться», и, опционально, действие, которое должно произойти,
//      когда стороны встретились, но перед тем когда они будут «отпущены».

        Runnable barrierAction = () -> System.out.println("Well done, guys!");

        ExecutorService executor = Executors.newCachedThreadPool();
        CyclicBarrier barrier = new CyclicBarrier(3, barrierAction);

        Runnable task = () -> {
            try {
                // simulating a task that can take at most 1sec to run
                System.out.println("Doing task for " + Thread.currentThread().getName());
                Thread.sleep(new Random().nextInt(10) * 100);
                System.out.println("Done for " + Thread.currentThread().getName());
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < 10; i++) {
            executor.execute(task);
        }
        executor.shutdown();

    }
}
