package org.example;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        long startTime = System.currentTimeMillis();

        SharedObject shared = new SharedObject();
        AtomicInteger[] accessCounts = new AtomicInteger[SharedObject.n];
        for (int i = 0; i < SharedObject.n; i++)
            accessCounts[i] = new AtomicInteger(0);

        MyThread[] threads = new MyThread[SharedObject.n];
        for (int i = 0; i < SharedObject.n; i++) {
            threads[i] = new MyThread(i, shared, accessCounts[i]);
            threads[i].start();
        }

        for (MyThread t : threads) t.join();

        long endTime = System.currentTimeMillis();

        System.out.println(shared.getCounter());
        System.out.println((endTime - startTime) + " ms");
        for (int i = 0; i < SharedObject.n; i++) {
            System.out.println("Thread " + i + ": " + accessCounts[i].get());
        }
    }
}
