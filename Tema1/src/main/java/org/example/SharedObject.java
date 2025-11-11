package org.example;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedObject {
    public static final int n = 4;
    public static final int limit = 150000;

    private final AtomicInteger counter = new AtomicInteger(0);

    private final AtomicBoolean[] flag = new AtomicBoolean[n];
    private final AtomicBoolean[] access = new AtomicBoolean[n];
    private final AtomicInteger[] label = new AtomicInteger[n];

    public SharedObject() {
        init();
    }

    private void init() {
        for (int k = 0; k < n; k++) {
            flag[k] = new AtomicBoolean(false);
            access[k] = new AtomicBoolean(false);
            label[k] = new AtomicInteger(k + 1);
        }
    }

    public int getCounter() {
        return counter.get();
    }

    public void lock(int i) {
        flag[i].set(true);
        do {
            access[i].set(false);

            boolean conditionMet;
            do {
                conditionMet = true;
                for (int j = 0; j < n; j++) {
                    if (j == i) continue;
                    if (flag[j].get() && label[j].get() < label[i].get()) {
                        conditionMet = false;
                        break;
                    }
                }

            } while (!conditionMet);

            access[i].set(true);

        } while (existsOtherAccess(i));
    }

    private boolean existsOtherAccess(int i) {
        for (int j = 0; j < n; j++) {
            if (j != i && access[j].get()) return true;
        }
        return false;
    }

    public void unlock(int i) {
        int maxLabel = 0;
        for (int k = 0; k < n; k++) {
            maxLabel = Math.max(maxLabel, label[k].get());
        }
        label[i].set(maxLabel + 1);
        access[i].set(false);
        flag[i].set(false);

        // c)
        int minLabel = Integer.MAX_VALUE;
        for (int k = 0; k < n; k++) {
            minLabel = Math.min(minLabel, label[k].get());
        }
        for (int k = 0; k < n; k++) {
            label[k].set(label[k].get() - minLabel + 1); // shiftam toate la valori mici
        }
    }

    public boolean incrementIfUnderLimit(int id) {
        lock(id);
        try {
            if (counter.get() >= limit) return false;
            counter.incrementAndGet();
            return true;
        } finally {
            unlock(id);
        }
    }
}
