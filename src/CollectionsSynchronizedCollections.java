import java.awt.*;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CollectionsSynchronizedCollections {

    public static void insertIfAbsent(Vector<Long> list, Long value) {
        synchronized (list) {
            boolean contains = list.contains(value);
            if (!contains) {
                list.add(value);
                System.out.println("Value added: " + value);
            }
        }
    }

    /**
     * You can have duplicates. Try to run multiple times and see the diff in
     * results
     */
    public static void insertIfAbsentUnsafe(Vector<Long> list, Long value) {
        boolean contains = list.contains(value);
        if (!contains) {
            list.add(value);
            System.out.println("Value added: " + value);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        // Synchronized - Vector
        Vector vector = new Vector<Long>();

        Runnable insertIfAbsent = () -> {
            long millis = System.currentTimeMillis() / 1000;
            insertIfAbsent(vector, millis);
        };
        for (int i = 0; i < 10001; i++) {
            executor.execute(insertIfAbsent);
        }
        executor.shutdown();
        executor.awaitTermination(4000, TimeUnit.SECONDS);

        // Using the wrappers for not sync collections
        // List<String> synchronizedList = Collections.synchronizedList(abcList);
        // Collections.synchronizedMap(m)
        // Collections.synchronizedXXX
    }

}
