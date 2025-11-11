package org.example;

import java.util.concurrent.atomic.AtomicInteger;

public class MyThread extends Thread {
    private final int id;
    private final SharedObject shared;
    private final AtomicInteger accessCount;

    public MyThread(int id, SharedObject shared, AtomicInteger accessCount) {
        this.id = id;
        this.shared = shared;
        this.accessCount = accessCount;
    }

    @Override
    public void run() {
        while (shared.incrementIfUnderLimit(id)) {
            accessCount.incrementAndGet();
        }
    }
}
