package ru.job4j.queue;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * пара нюансов:
 * - используем CopyOnWriteArrayList (можно было Collections.synhronized(лист) для обычного листа)
 * - ждем пока все потоки отработают (тт.е. джойны) и лишь потом печатаем 2 списка
 */

public class QueueEngine {
    public static void main(String[] args) throws InterruptedException {
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue(2);

        List<Integer> numbersToPut = new CopyOnWriteArrayList<>();
        List<Integer> numbersToGain = new CopyOnWriteArrayList<>();

        Runnable producerRunnable = () -> {
            try {
                int offered = new Random().nextInt();
                queue.offer(offered);
                numbersToPut.add(offered);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread producer1 = new Thread(
                producerRunnable
        );
        Thread producer2 = new Thread(
                producerRunnable
        );

        Runnable consumerRunnable = () -> {
            try {
                Integer polled = queue.poll();
                numbersToGain.add(polled);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread consumer1 = new Thread(
                consumerRunnable
        );
        Thread consumer2 = new Thread(
                consumerRunnable
        );

        producer1.start();
        producer2.start();
        Thread.sleep(1000);
        consumer1.start();
        consumer2.start();

        producer1.join();
        producer2.join();
        consumer1.join();
        consumer2.join();

        System.out.println(numbersToPut);
        System.out.println(numbersToGain);
    }
}
