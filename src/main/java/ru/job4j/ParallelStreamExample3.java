package ru.job4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * parallel() - хранит порядок элементов, а вот для peek() на него всее равно
 * (поэтому и есь иллюзия, что порядок уже не важен)
 * ЛИСТ - есть порядок, СЕТ - нет.
 * для некоторых операций оно важно, т.к. грубо говоря еесли порядок уже был в СД, за ним еще надо будт присматривать
 * (что может сказываться н производителньости - для оптимзации можно применять unordered(),
 * если порядок финальный не важен)
 * И да: peek() вообще имеет побоный эффект, которому насрать на порядок (в многопоточке!)...
 *
 * list.stream().parallel().forEach(System.out::println); - форичу - тоже
 * forEachOrdered - сравни с ним
 *
 * Но- да: но совсем не факт, что если нам все еще нужен порядок,
 * анордеред + финальный сорт будут лучше по производительности...
 */

public class ParallelStreamExample3 {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list1 = list.stream().parallel().peek(System.out::println).toList();
        System.out.println(list1);

        list.stream().parallel().forEach(System.out::println);

        System.out.println();
        list.stream().parallel().forEachOrdered(System.out::println);

        System.out.println();
        List<Integer> list2 = list1.stream().unordered().toList();
        System.out.println(list2);
    }
}
