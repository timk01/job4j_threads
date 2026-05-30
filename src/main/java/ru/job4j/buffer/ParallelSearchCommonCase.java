package ru.job4j.buffer;

import ru.job4j.queue.SimpleBlockingQueue;

public class ParallelSearchCommonCase {

    /**
     * blocking consumer может ждать бесконечно, если producer уже закончил и новых элементов не будет.
     * (
     * для того чтобы как тут НЕ крутился в бесконеном цикле, например изначально в задаче:
     * while (true) {
     * System.out.println(queue.poll());
     * }
     * )
     * --- нужны интеррапты, флаги или еще какое-то управление!!
     * <p>
     * Базовая логика SimpleBlockingQueue конкретно метода POLL():
     * Если очередь не пустая — poll() сразу вернул элемент, consumer печатает и идёт на следующий круг.
     * Если очередь пустая — consumer заходит внутрь poll() и зависает на wait().
     * очередь пустая → poll() заходит в wait() и ждёт.
     * wait() выбросит InterruptedException не из-за пустой очереди,
     * а если другой поток вызовет consumer.interrupt() пока consumer находится в wait() ((здесь - мейн))
     * <p>
     * Здесь ВАЖЕН общий порядок:
     * consumer.start(); - уже стартовали консьюмера
     * producer.start(); - уже стартовали и продьюсера
     * producer.join(); - подождали пка продьюсер полностью отрабоатет (в очереди 3 элемента, но паралелльно работает
     * консьюмер и берет элементы) ***
     * !!!
     * ПРЕДПОЛАГАЕМ (СИЛЬНОЕ ДОПУЩЕНИЕ), что к моменту consumer.interrupt(); - консьюмер а) уже оббработал все элементы,
     * что туда положил продьюсер, б) успешно зашел в цикл (иначе consumer.interrupt() = интеррапт = тру --
     * условие цикла = фолс и мы в цикл даже и н провалися) и находится в состонии вейта - сработает слчай 2
     * !!!
     * <p>
     * (В ОБЩЕМ СЛУЧАЕ, consumer крутится много раз в цикле и на каждом круге проверяет isInterrupted()),
     * если так вышло что он не успел обработать все элементы - он тупо туда не зайдет
     * - отюдса и ремарка что допущение реально "сильное")
     * 1 кейс: все хорошо. If none of the previous conditions hold then this thread's interrupt status will be set.
     * 2 кейс. мы уже в авайте. интеррапт разбудит вейт и выбьет исключение, а чтобы ввыйти из самого цикла, т.к. статус
     * был = фолс, нам надо его еще раз вручную измнить, т.е. Thread.currentThread().interrupt();
     * <p>
     * короче, в ОБЩЕМ случае тут много допущений...
     *
     * @param args
     * @throws InterruptedException
     */

    public static void main(String[] args) throws InterruptedException {
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(1);
        final Thread consumer = new Thread(
                () -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            System.out.println(queue.poll());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
        );
        consumer.start();
        Thread.sleep(1000);

        Thread producer = new Thread(
                () -> {
                    for (int index = 0; index != 3; index++) {
                        try {
                            queue.offer(index);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );

        producer.start();
        producer.join();

        consumer.interrupt();
        consumer.join();
    }
}