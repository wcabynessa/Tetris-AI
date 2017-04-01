import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;


public class ThreadController {

    private static final int CORE_POOL_SIZE = 20;
    private static final int MAX_POOL_SIZE = 50;
    private static final long KEEP_ALIVE_TIME = 50000;


    private static final ThreadController INSTANCE = new ThreadController();

    private final ThreadPoolExecutor executor;

    private ThreadController() {
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                                            MAX_POOL_SIZE,
                                            KEEP_ALIVE_TIME,
                                            TimeUnit.SECONDS,
                                            new LinkedBlockingQueue<Runnable>());
    }

    public ThreadController getInstance() {
        return INSTANCE;
    }

    public void submitTask(Thread thread) {
        executor.execute(thread);
    }

    public void awaitFinish() {
        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("ERROR: Thread execution interrupted");
            System.exit(1);
        }
    }
}
