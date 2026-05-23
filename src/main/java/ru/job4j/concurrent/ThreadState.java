package ru.job4j.concurrent;

public class ThreadState {
    /**
     * while (first.isAlive() || second.isAlive()) {
     * }
     * System.out.println("работа завершена");
     * <p>
     * можно бы было так (без дожйнов), логика:
     * мы будем крутиться пока оба НЕ фолс (т.е. вылезем из цикла только когда оба мертвы)
     * <p>
     * -- ИЛИ можно прямо вообще в терминейтед упороться:
     * while (first.getState() != Thread.State.TERMINATED || second.getState() != Thread.State.TERMINATED) {
     * }
     * System.out.println("работа завершена");
     * <p>
     * Более верный вариант - с джойнами:
     * first.join();
     * second.join();
     * System.out.println("работа завершена");
     *
     * @param args
     * @throws InterruptedException
     */

    public static void main(String[] args) throws InterruptedException {
        Thread first = new Thread(
                () -> System.out.println(Thread.currentThread().getName())
        );
        Thread second = new Thread(
                () -> System.out.println(Thread.currentThread().getName())
        );

        first.start();
        second.start();

        while (first.getState() != Thread.State.TERMINATED || second.getState() != Thread.State.TERMINATED) {
            Thread.sleep(10);
        }
        System.out.println("работа завершена");
    }
}
