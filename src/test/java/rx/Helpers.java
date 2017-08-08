package rx;

import java.util.Random;

import static java.lang.Math.abs;

public class Helpers {
    private static final Random r = new Random();

    public static void log(String message) {
        System.out.println(Thread.currentThread().getName() + " | " + message);
    }

    public static void log(int i) {
        log(Integer.toString(i));
    }

    public static void log(long i) {
        log(Long.toString(i));
    }
    public static void log(boolean b) {
        log(Boolean.toString(b));
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log("Interrupted");
        }
    }

    public static long randomLong(long bound) {
        return abs(r.nextLong() % bound);
    }
}
