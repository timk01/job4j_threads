package ru.job4j.concurrent;

import java.util.stream.IntStream;

/**
 * sequential() = однопоточная обработка stream
 * parallel() = многопоточная обработка stream (если грубо, многопоточка, если она возможна в принципе)
 */
public class ParallelStreamExample {
    public static void main(String[] args) {
        IntStream intStream = IntStream.range(1, 100).parallel();
        System.out.println(intStream.isParallel());
        IntStream stream = intStream.sequential();
        System.out.println(stream.isParallel());
    }
}
