import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.Executors;

/**
 * FutureTask<V> represents an asynchronous computation. It has methods to check
 * if the computation is completed and to cancel it if needed.
 *
 * Used for computing long running tasks/IO tasks.
 *
 * Act as a latch and has three states: waiting for run, running or completed.
 * Also, it can be easy canceled with the cancel() method.
 *
 * if the result is ready, get() will return the value. Otherwise, it'll block.
 *
 */

public class FutureTasks1 {

    public static void main(String[] args) {
        Callable<Integer> callable = () -> {
            int random = new Random().nextInt(10) * 100;
            System.out.println("Preparing to execute");
            Thread.sleep(random);
            System.out.println("Executed - " + random);
            return random;
        };

        FutureTask<Integer> futureTask = new FutureTask<>(callable);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(futureTask);

        try {
            Integer value = futureTask.get(2, TimeUnit.SECONDS);
            System.out.println("Value is " + value);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }


}
