package org.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        int numberOfThreads = 1000;
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                String route = generateRoute("RLRFR", 100);
                int countR = countOccurrences(route, 'R');

                // Обновление частот в потокобезопасной мапе
                synchronized (sizeToFreq) { // Синхронизация доступа к мапе
                    sizeToFreq.put(countR, sizeToFreq.getOrDefault(countR, 0) + 1);
                }
            });
            threads[i].start();
        }

        // Ждем завершения всех потоков.
        for (Thread thread : threads) {
            thread.join();
        }

        // Вывод результатов
        printResults();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static int countOccurrences(String route, char c) {
        int count = 0;
        for (char ch : route.toCharArray()) {
            if (ch == c) {
                count++;
            }
        }
        return count;
    }

    public static void printResults() {
        // Поиск самого частого количества повторений
        int maxFrequency = 0;
        int mostFrequentCount = 0;

        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostFrequentCount = entry.getKey();
            }
        }

        System.out.println("Самое частое количество повторений " + mostFrequentCount + " (встретилось " + maxFrequency + " раз)");
        System.out.println("Другие размеры:");
        int finalMostFrequentCount = mostFrequentCount;
        sizeToFreq.forEach((size, freq) -> {
            if (size != finalMostFrequentCount) {
                System.out.println("- " + size + " (" + freq + " раз)");
            }
        });
    }
}