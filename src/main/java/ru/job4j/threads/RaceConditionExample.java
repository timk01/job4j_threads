package ru.job4j.threads;

public class RaceConditionExample {
    public static int number = 0;

    /**
     * int current = number;
     * int next = ++number;
     *
     * - вот здесь может вклиниться другой поток (++number - не атомарен)
     * и да, джойны не нужны, нам в принципе не важен намбер в конце, т.е. дожидаться окончания работы не обязательно
     *
     * @param args
     */
    public static void main(String[] args) {
        Runnable task = () -> {
            for (int i = 0; i < 99999; i++) {
                int current = number;
                int next = ++number;
                if (current + 1 != next) {
                    throw new IllegalStateException("Некорректное сравнение: " + current + " + 1 = " + next);
                }
            }
        };
        new Thread(task).start();
        new Thread(task).start();
    }
}