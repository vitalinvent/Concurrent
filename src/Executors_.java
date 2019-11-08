import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class Executors_ {


//    newSingleThreadExecutor() использует только один поток, что равнозначно:
//    newFixedThreadPool(1). Но есть отличие от эквивалентного newFixedThreadPool(1),
//    в том, что возвращенный исполнитель гарантированно не может быть перенастроен
//    для использования дополнительных потоков.


    public static void usingSingleThreadExecutor() {
        System.out.println("=== SingleThreadExecutor ===");
        ExecutorService singleThreadExecutor = java.util.concurrent.Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(() -> System.out.println("Print this."));
        singleThreadExecutor.execute(() -> System.out.println("and this one to."));
        singleThreadExecutor.shutdown();
        try {
            singleThreadExecutor.awaitTermination(4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\n\n");
    }


//    newCachedThreadPool Создает пул потоков, который создает новый потоки по мере необходимости,
//    но будут использоваться повторно ранее построенные потоки, когда они доступны.

    public static void usingCachedThreadPool() {
        System.out.println("=== CachedThreadPool ===");
        ExecutorService cachedThreadPool = java.util.concurrent.Executors.newCachedThreadPool();
        LinkedList<Future<UUID>> uuids = new LinkedList<Future<UUID>>();
        for (int i = 0; i < 10; i++) {
            Future submittedUUID = cachedThreadPool.submit(() -> {
//				Thread.sleep(new Random().nextInt(100)*100);
                UUID randomUUID = UUID.randomUUID();
                System.out.println("UUID " + randomUUID + " from " + Thread.currentThread().getName());
                return randomUUID;
            });
            uuids.add(submittedUUID);
        }
        cachedThreadPool.execute(() -> uuids.forEach((f) -> {
            try {
                System.out.println("Result " + f.get() + " from thread " + Thread.currentThread().getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }));
        cachedThreadPool.shutdown();
        try {
            cachedThreadPool.awaitTermination(4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\n\n");

    }


//    usingFixedThreadPool Создает пул потоков, который повторно использует фиксированное
//    количество отключенных потоков общая неограниченная очередь. не больше n потоков

    public static void usingFixedThreadPool() {
        System.out.println("=== FixedThreadPool ===");
        ExecutorService fixedPool = java.util.concurrent.Executors.newFixedThreadPool(4);
        LinkedList<Future<UUID>> uuids = new LinkedList<Future<UUID>>();
        for (int i = 0; i < 20; i++) {
            Future submitted = fixedPool.submit(() -> {
                UUID randomUUID = UUID.randomUUID();
                System.out.println("UUID " + randomUUID + " from " + Thread.currentThread().getName());
                return randomUUID;
            });
            uuids.add(submitted);
        }
        fixedPool.execute(() -> uuids.forEach((f) -> {
            try {
                System.out.println("Result " + f.get() + " from " + Thread.currentThread().getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }));
        fixedPool.shutdown();
        try {
            fixedPool.awaitTermination(4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\n\n");
    }

//    ScheduledExecutorService позволяет поставить код выполняться в одном или нескольких
//    потоках и сконфигурировать интервал или время, на которое выполненение будет отложено.

    public static void usingScheduledThreadPool() {
        System.out.println("=== ScheduledThreadPool ===");
        ScheduledExecutorService scheduledThreadPool = java.util.concurrent.Executors.newScheduledThreadPool(4);
        scheduledThreadPool.scheduleAtFixedRate(() -> System.out.println("1) Print every 2s"), 0, 2, TimeUnit.SECONDS);
        scheduledThreadPool.scheduleAtFixedRate(() -> System.out.println("2) Print every 2s"), 0, 2, TimeUnit.SECONDS);
        scheduledThreadPool.scheduleWithFixedDelay(() -> System.out.println("3) Print every 2s delay"), 0, 2,
                TimeUnit.SECONDS);

        try {
            scheduledThreadPool.awaitTermination(6, TimeUnit.SECONDS);
            scheduledThreadPool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\n\n");
    }



    public static void usingSingleTreadScheduledExecutor() {
        System.out.println("=== SingleThreadScheduledThreadPool ===");
        ScheduledExecutorService singleThreadScheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
        singleThreadScheduler.scheduleAtFixedRate(() -> System.out.println("1) Print every 2s"), 0, 2, TimeUnit.SECONDS);
        singleThreadScheduler.scheduleWithFixedDelay(() -> System.out.println("2) Print every 2s delay"), 0, 2,
                TimeUnit.SECONDS);

        try {
            singleThreadScheduler.awaitTermination(6, TimeUnit.SECONDS);
            singleThreadScheduler.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\n\n");

    }


    //    newWorkStealingPool creates a workstealing-utilizing thread pool with the number
    //    of threads as the number of processors.

    //    newWorkStealingPool presents a new problem. if I have four logical cores, then the
    //    pool will have four threads total. if my tasks block - for example on synchronous
    //    IO - I don't utilize my CPUs enough. what I want is four active threads at any
    //    given moment, for example - four threads which encrypt AES and another 140 threads
    //    which wait for the IO to finish.


    public static void usingWorkStealingThreadPool() {
        System.out.println("=== WorkStealingThreadPool ===");
        ExecutorService workStealingPool = java.util.concurrent.Executors.newWorkStealingPool();

        workStealingPool.execute(() -> System.out.println("Prints normally"));

        Callable<UUID> generatesUUID = UUID::randomUUID;
        LinkedList<Callable<UUID>> severalUUIDsTasks = new LinkedList<Callable<UUID>>();
        for (int i = 0; i < 20; i++) {
            severalUUIDsTasks.add(generatesUUID);
        }

        try {
            List<Future<UUID>> futureUUIDs = workStealingPool.invokeAll(severalUUIDsTasks);
            int count=0;
            for (Future<UUID> future : futureUUIDs) {
                if (future.isDone()) {
                    UUID uuid = future.get();
                    count++;
                    System.out.println("New UUID :" + uuid);
                }
            }
            System.out.println("done - "+count+" threads");


        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        try {
            workStealingPool.awaitTermination(6, TimeUnit.SECONDS);
            workStealingPool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\n\n");
    }

    public static void main(String[] args) {
		usingSingleThreadExecutor();
		usingCachedThreadPool();
		usingFixedThreadPool();
		usingScheduledThreadPool();
		usingSingleTreadScheduledExecutor();
        usingWorkStealingThreadPool();
    }

}
