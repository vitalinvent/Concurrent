import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocksIntrinsicLock {


    private boolean state;

    /**
     * When used in method signature, synchronized use 'this' as a lock.
     *
     * Instead of 'this', other objects variables can be used
     */
    public synchronized void mySynchronizedMethod() {
        state = !state;
        // Everything in this method can only be accessed by the thread who hold the
        // lock.
        System.out.println("My state is:" + state);
        // Without sync: states have no order guarantee true, true, false, true...
        // With sync: always true, false, true, false...
    }

    /**
     * It's possible to lock only a block inside the method
     */
    public void mySynchronizedBlock() {
        /*
         * Everything in this block can only be accessed by the thread who hold the
         * lock. The message bellow will be printed before the message inside the
         * synchronized block
         */
        System.out.println("Who owns my lock: " + Thread.currentThread().getName());
        synchronized (this) {
            state = !state;
            System.out.println("Who owns my lock after state changes: " + Thread.currentThread().getName());
            System.out.println("State is: " + state);
            System.out.println("====");
        }
    }

    /**
     * Already holds a lock when called
     */
    public synchronized void reentrancy() {
        System.out.println("Before acquiring again");
        // Tries to hold it without releasing the lock
        synchronized (this) {
            System.out.println("I'm own it! " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        LocksIntrinsicLock self = new LocksIntrinsicLock();
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> self.mySynchronizedMethod());
        }
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> self.mySynchronizedBlock());
        }
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> self.reentrancy());
        }
        executor.shutdown();
    }


}
