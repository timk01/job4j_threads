package ru.job4j.buffer;

import ru.job4j.queue.SimpleBlockingQueue;

public class ParallelSearchPoisonPill {

    /**
     * пойзон пилл = в конец прода засоываем -1 (или другую оговоренную циферу)
     * консьюмер - дойдя до нее, а она в конце - просто делает брейк из цикла
     * интрееррапты снова не нужны
     *
     * @param args
     * @throws InterruptedException
     */

    public static void main(String[] args) throws InterruptedException {
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(1);
        final Thread consumer = new Thread(
                () -> {
                    while (true) {
                        try {
                            Integer polled = queue.poll();
                            if (polled == -1) {
                                break;
                            }
                            System.out.println(polled);
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
                    try {
                        queue.offer(-1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        producer.start();

        producer.join();
        consumer.join();
    }
}