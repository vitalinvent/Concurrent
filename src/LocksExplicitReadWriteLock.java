import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocksExplicitReadWriteLock {

    // Equivalent to Intrinsic Locks
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private String myContent = "A long default content......";

    /**
     * Simplest way to use the read mode
     *
     * @return
     */
    public String showContent() {
        ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
            System.out.println("Reading state while holding a lock.");
            return myContent;
        } finally {
            readLock.unlock();
        }
    }

    public void writeContent(String newContentToAppend) {
        ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            System.err.println("Writing " + newContentToAppend);
            myContent = new StringBuilder().append(myContent).append(newContentToAppend).toString();
        } finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        LocksExplicitReadWriteLock self = new LocksExplicitReadWriteLock();
        // Readers
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                try {
                    // Delay readers to start
                    Thread.sleep(new Random().nextInt(10) * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(self.showContent());
            });
        }

        // Writers - only if no writer is available
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> self.writeContent(UUID.randomUUID().toString()));
        }
        executor.shutdown();
    }

}
