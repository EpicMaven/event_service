
package org.novalabs.event.util;

public class TimingUtils {
    public TimingUtils() {
    }

    public static long duration(long startMillis) {
        return duration(startMillis, System.currentTimeMillis());
    }

    public static long duration(long startMillis, long endMillis) {
        return endMillis - startMillis;
    }
}
