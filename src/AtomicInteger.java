import java.util.concurrent.Semaphore;


public class AtomicInteger {

    private Semaphore sem = new Semaphore(1);
    private int value;

    public AtomicInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void updateValue(int newValue) throws InterruptedException {
        sem.acquire();
        value = Math.max(value, newValue);
        sem.release();
    }
}
