
public class Utility {

    /** Get max of array sequence from starting (inclusive) to ending (exclusive) */
    public static int arrayMax(int[] arr, int starting, int ending) {
        int ans = 0;
        for (int i = starting;  i < ending;  i++) {
            ans = Math.max(ans, arr[i]);
        }
        return ans;
    }
}
