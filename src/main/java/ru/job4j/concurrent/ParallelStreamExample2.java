package ru.job4j.concurrent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ParallelStreamExample2 {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Stream<Integer> parallelStream = list.parallelStream();
        System.out.println(parallelStream.isParallel());
        Optional<Integer> result = parallelStream.reduce((left, right) -> left * right);
        result.ifPresent(System.out::println);
    }
}
