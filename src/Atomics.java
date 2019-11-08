import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Atomics {

//    при использовании атомарных операций set/get
//    За счет использования CAS, операции с этими классами работают быстрее, чем если
//    синхронизироваться через synchronized/volatile. Плюс существуют методы для атомарного
//    добавления на заданную величину, а также инкремент/декремент.


    /*
     * A Counter using AtomicInteger
     */
    static class AtomicCounter {
        private AtomicInteger atomicInteger = new AtomicInteger(0);

        public void increment() {
            atomicInteger.incrementAndGet();
        }

        public void decrement() {
            atomicInteger.decrementAndGet();
        }

        public int get() {
            return atomicInteger.get();
        }

//		private Integer atomicInteger = 0;
//
//		public void increment() {
//			atomicInteger++;
//		}
//
//		public void decrement() {
//			atomicInteger--;
//		}
//
//		public int get() {
//			return atomicInteger;
//		}
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicCounter counter = new AtomicCounter();
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10_000; i++) {
            cachedThreadPool.execute(() -> counter.increment());
        }
        cachedThreadPool.shutdown();
        cachedThreadPool.awaitTermination(4000, TimeUnit.SECONDS);
        System.out.println("Result shound be 10000: Actual result is: " + counter.get());
    }

}
