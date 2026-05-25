package ru.job4j.concurrent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NotParallelStreamExample {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> multiplication = list.stream()
                .reduce((left, right) -> left * right); 
        System.out.println(multiplication.get()); 
    }
}