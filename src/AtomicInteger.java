import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;
import java.util.ArrayList;


public class AtomicInteger {

    private Semaphore sem = new Semaphore(1);
    private ArrayList<Integer> values = new ArrayList<Integer>();

    public AtomicInteger(int value) {
    }

    public int getValue() {
        int sum = 0;
        for (int value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    public void updateValue(int newValue) throws InterruptedException {
        sem.acquire();
        values.add(newValue);
        sem.release();
    }

    @Override
    public String toString() {
        return Integer.toString(getValue());
    }
}
