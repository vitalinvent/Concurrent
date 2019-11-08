import javax.xml.stream.FactoryConfigurationError;
import java.math.BigInteger;
import java.sql.Time;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class WorkStealingForkJoin {


//    Задача для сервиса представляется экземпляром класса ForkJoinTask. В основном используются
//    подклассы RecursiveTask и RecursiveAction, для задач с результатом и без соответственно.
//    Аналогично интерфейсам Callable и Runnable обычного ExecutorService.
//    Тело рекурсивной операции задается в реализации метода compute() задачи ForkJoinTask.
//    Здесь же создаются новые подзадачи, и запускаются параллельно методом fork().
//    Чтобы дождаться завершения выполнения задачи, на каждой форкнутой подзадаче вызывается
//    блокирующий метод join(), результат выполнения при необходимости агрегируется.
//    С точки зрения использования метод ForkJoinTask.join() похож на аналогичный метод класса
//    Thread. Но в случае fork-join поток может на самом деле не заснуть, а переключиться на
//    выполнение другой задачи. Такая стратегия называется work stealing, и позволяет
//    эффективнее использовать ограниченное количество потоков. Это похоже на переиспользование
//    потоков корутинах Kotlin (green threads).


    /**
     * Common Pool
     *
     * Default instance of a fork join pool in a Java app, used by
     * CompletableFuture, and parallel streams. All threads used by the common pool
     * can be reused, released and reinstated after some time. This approach reduces
     * the resource consumption. It doesn't need to be closed/shutdown.
     *
     */
    public ForkJoinPool getCommonPool() {
        return ForkJoinPool.commonPool();
    }

    /**
     * Customize ForkJoinPool
     *
     * Parallelism: Parallelism level, default is Runtime#availableProcessors
     *
     * ForkJoinWorkerThreadFactory: Factory used for creating threads for the pool.
     *
     * UncaughtExceptionHandler: handles worker threads that terminates due some
     * "unrecoverable" problem.
     *
     * True-value AsyncMode: FIFO scheduling mode, used by tasks that are never
     * joined, like event-oriented asynchronous tasks.
     *
     */
    public ForkJoinPool customForkJoinPool(int parallelism,
                                           ForkJoinPool.ForkJoinWorkerThreadFactory factory,
                                           Thread.UncaughtExceptionHandler handler,
                                           boolean asyncMode) {
        return new ForkJoinPool(parallelism, factory, handler, asyncMode);
    }

    /**
     *
     * Tasks
     *
     * ForkJoinTask is the base type of a task. It represents a "lightweight
     * thread", with the ForkJoinPool being it's scheduler.
     *
     * RecursiveTask: Task that returns a value, result of a computation.
     *
     * RecursiveAction: Task that doesn't returns a value.
     *
     * Both can be used to implement the workflow algorithm described in the
     * Workflow section, with he aid of Fork and Join.
     *
     */

    /**
     * RecursiveTask
     *
     * Represents a result of a computation.
     *
     * In the example bellow, it follows the algorithm, partitioning the numbers
     * list in half, using fork and join to control the task flow.
     *
     */
    static class qqq extends RecursiveTask<Long> {

        @Override
        protected Long compute() {
            return null;
        }
    }

    static class RecSumTask extends RecursiveTask<BigInteger> {

        private static final long serialVersionUID = 1L;
        public static final int DIVIDE_AT = 500;

        private List<Integer> numbers;

        public RecSumTask(List<Integer> numbers) {
            this.numbers = numbers;
        }

        @Override
        protected BigInteger compute() {
            LinkedList<RecSumTask> subTasks = new LinkedList<RecSumTask>();
            if (numbers.size() < DIVIDE_AT) {
                // directly
                BigInteger subSum = BigInteger.ZERO;
                for (Integer number : numbers) {
                    subSum = subSum.add(BigInteger.valueOf(number));
                }
                return subSum;
            } else {
                // Divide to conquer
                int size = numbers.size();
                List<Integer> numbersLeft = numbers.subList(0, size / 2);
                List<Integer> numbersRight = numbers.subList(size / 2, size);

                RecSumTask recSumLeft = new RecSumTask(numbersLeft);
                RecSumTask recSumRight = new RecSumTask(numbersRight);

                subTasks.add(recSumRight);
                subTasks.add(recSumLeft);

                // Fork Child Tasks
                recSumLeft.fork();
                recSumRight.fork();
            }

            BigInteger sum = BigInteger.ZERO;
            for (RecSumTask recSum : subTasks) {
                // Join Child Tasks
                sum = sum.add(recSum.join());
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        long startTime= System.currentTimeMillis();
        // prepares dataset for the example
        LinkedList<Integer> numbers = new LinkedList<Integer>();
        for (int i = 0; i < 500_000; i++) {
            numbers.add(i);
        }

        // Usage
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        RecSumTask task = new RecSumTask(numbers);
        BigInteger result = commonPool.invoke(task);
        System.out.println("Result is: " + result);
        System.out.println("\n\n");
        System.out.println(startTime-System.currentTimeMillis());
    }

    /**
     * RecursiveTask
     *
     * Represents a result of a computation, resembles RecursiveTask, but without
     * the return value.
     *
     */
    static class ARecursiveAction extends RecursiveAction {

        private static final long serialVersionUID = 1L;

        @Override
        protected void compute() {
            // same pattern goes here
        }

    }

    /**
     * It's possible to extract informations about the pool's current state.
     *
     * Active thread count: Number of threads that are stealing or executing tasks.
     *
     * Pool size: Number of worker threads that are started but not terminated yet.
     *
     * Parallelism level: Equivalent to the number of available processors.
     *
     * Queue submitted tasks: Number of submitted tasks, but not executing. Steal
     * count:
     *
     * Number of stealed tasks from a thread to another, useful for monitoring.
     *
     */
    public static void debugPool(ForkJoinPool commonPool) {
        System.out.println("Debuggin ForJoinPool");
        System.out.println("Active Thread Count: " + commonPool.getActiveThreadCount());
        System.out.println("Pool Size: " + commonPool.getPoolSize());
        System.out.println("Parallelism level: " + commonPool.getParallelism());
        System.out.println("Queue submitted tasks: " + commonPool.getQueuedSubmissionCount());
        System.out.println("Steal count: " + commonPool.getStealCount());
        System.out.println("\n");
    }


}
