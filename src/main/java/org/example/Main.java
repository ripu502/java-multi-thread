package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final int DEFAULT_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("DEFAULT_THREAD_POOL_SIZE: " + DEFAULT_THREAD_POOL_SIZE);
        ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
        List userDataList = generateUserDataList(1000);

        try {

            List<CompletableFuture<Void>> futures = new ArrayList<>();

            AtomicInteger successCount = new AtomicInteger();
            AtomicInteger failCount = new AtomicInteger();

            for (Object userData : userDataList) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        makeAPICallMock(userData);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }
                }, executor);

                futures.add(future);
            }

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();

            System.out.println("Total Success: " + successCount.get());
            System.out.println("Total Fail: " + failCount.get());

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Total execution time: " + totalTime + " milliseconds");


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

    }

    private static void makeAPICallMock(Object userData) {
        System.out.println("userData = " + userData);
        sleepForOneSecond();
    }

    public static void sleepForOneSecond() {
        try {
            Thread.sleep(1000); // Sleep for 1 second (1000 milliseconds)
        } catch (InterruptedException e) {
            // Handle interrupted exception if needed
            e.printStackTrace();
        }
    }

    private static List<Integer> generateUserDataList(int size) {
        List userDataList = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            userDataList.add(i); // Just filling the array with sample data (0, 1, 2, ..., size-1)
        }
        return userDataList;
    }
}