package ru.job4j.queue;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleBlockingQueueTest {

    @Test
    public void whenProducersEqualConsumerThenAllGood() throws InterruptedException {
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(2);

        List<Integer> numbersToPut = new CopyOnWriteArrayList<>();
        List<Integer> numbersToGain = new CopyOnWriteArrayList<>();

        Runnable producerRunnable = () -> {
            try {
                int offered1 = 1;
                int offered2 = 10;
                queue.offer(offered1);
                queue.offer(offered2);
                numbersToPut.add(offered1);
                numbersToPut.add(offered2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread producer1 = new Thread(
                producerRunnable
        );

        Runnable consumerRunnable = () -> {
            try {
                numbersToGain.add(queue.poll());
                numbersToGain.add(queue.poll());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread consumer1 = new Thread(
                consumerRunnable
        );

        producer1.start();
        Thread.sleep(1000);
        consumer1.start();

        producer1.join();
        consumer1.join();

        assertThat(numbersToPut).containsExactlyInAnyOrderElementsOf(numbersToGain);
    }

    /**
     * consumer стартует
     * queue пустая
     * consumer засыпает на wait() в poll() ((т.е. в СД класть ПОКА нечего))
     * <p>
     * producer стартует
     * кладёт элемент через offer()
     * делает notifyAll()
     * <p>
     * в этот момент времени ТЕОРЕТИЧЕСКИ спящий консьюмер может проснуться и забрать из очереди элемент,
     * но т.к. мы не можем гаранировать того что все потоки отработают в корректном порядкее, мы и делаем джойн обоих
     * <p>
     * producer1.join();
     * consumer1.join();
     * - здесь текууйщий (в нашем случае мейн) поток ждет, пока они оба отработают
     *
     * @throws InterruptedException
     */

    @Test
    public void whenConsumerAwaitsProducer() throws InterruptedException {
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(2);

        List<Integer> numbersToPut = new CopyOnWriteArrayList<>();
        List<Integer> numbersToGain = new CopyOnWriteArrayList<>();

        Runnable consumerRunnable = () -> {
            try {
                numbersToGain.add(queue.poll());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread consumer1 = new Thread(
                consumerRunnable
        );

        consumer1.start();
        Thread.sleep(1000);

        assertThat(consumer1.isAlive()).isTrue();

        Runnable producerRunnable = () -> {
            try {
                int offered1 = 1;
                queue.offer(offered1);
                numbersToPut.add(offered1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread producer1 = new Thread(
                producerRunnable
        );

        producer1.start();

        producer1.join();
        consumer1.join();

        assertThat(producer1.isAlive()).isFalse();
        assertThat(consumer1.isAlive()).isFalse();
        assertThat(numbersToPut).containsExactlyInAnyOrderElementsOf(numbersToGain);
    }

    /**
     * capacity = 2
     * <p>
     * producer кладёт 1
     * producer кладёт 10
     * очередь полная: producer пытается положить 100 и - зависает на offer() (ккап = 2)
     * ((запускаем его и ждем, чтобы убедиться, что успеет полностью отработать до консьюмера))
     * <p>
     * consumer делает poll() - забирает ПЕРВЫЙ элемент "Retrieves and removes the head of this queue" - т.е. 1
     * освобождает место
     * <p>
     * producer просыпается
     * кладёт 100 (10, 100), в очередь куда клали = 1, 10, 100, в реальной = 10, 100
     *
     * @throws InterruptedException
     */

    @Test
    public void whenProducerAwaitsConsumer() throws InterruptedException {
        SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(2);

        List<Integer> numbersToPut = new CopyOnWriteArrayList<>();
        List<Integer> numbersToGain = new CopyOnWriteArrayList<>();

        Runnable producerRunnable = () -> {
            try {
                int offered1 = 1;
                int offered2 = 10;
                queue.offer(offered1);
                queue.offer(offered2);
                numbersToPut.add(offered1);
                numbersToPut.add(offered2);

                int offered3 = 100;
                queue.offer(offered3);
                numbersToPut.add(offered3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread producer1 = new Thread(
                producerRunnable
        );

        producer1.start();

        Thread.sleep(1000);

        assertThat(producer1.isAlive()).isTrue();
        assertThat(numbersToPut).containsExactly(1, 10);

        Runnable consumerRunnable = () -> {
            try {
                numbersToGain.add(queue.poll());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread consumer1 = new Thread(
                consumerRunnable
        );

        consumer1.start();

        producer1.join();
        consumer1.join();

        assertThat(producer1.isAlive()).isFalse();
        assertThat(consumer1.isAlive()).isFalse();
        assertThat(numbersToGain.size()).isEqualTo(1);
        assertThat(numbersToGain).containsExactly(1);
        assertThat(numbersToPut).containsExactly(1, 10, 100);
    }

    /**
     * !queue.isEmpty() || !Thread.currentThread().isInterrupted()
     * - очень сильно перекликается с ParallelSearchCommonCase
     * т.е. тут еще добавляется и это:
     * о есть после interrupt() consumer всё ещё может дочитать остатки из очереди, если они там есть.
     *
     * (!!!т.е. да, опять же НЕ ФАКТ, что к моменту первого интеррапта там прочитали все элменты из прода,
     * отсюда и двойная проверка и вот ЗДЕСЬ это к месту!!!)
     * @throws InterruptedException
     */

    @Test
    public void whenFetchAllThenGetIt() throws InterruptedException {
        final CopyOnWriteArrayList<Integer> buffer = new CopyOnWriteArrayList<>();
        final SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(5);
        Thread producer = new Thread(
                () -> {
                    for (int index = 0; index < 5; index++) {
                        try {
                            queue.offer(index);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
        producer.start();
        Thread consumer = new Thread(
                () -> {
                    while (!queue.isEmpty() || !Thread.currentThread().isInterrupted()) {
                        try {
                            buffer.add(queue.poll());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                }
        );
        consumer.start();
        producer.join();
        consumer.interrupt();
        consumer.join();
        assertThat(buffer).containsExactly(0, 1, 2, 3, 4);
    }

    /**
     * самый простой тест - ин=аут, количества равны, потому и интеррапт/хитрые услоовия выходов также не нужны
     * @throws InterruptedException
     */

    @Test
    public void whenProducedElementsEqualConsumedElements() throws InterruptedException {
        CopyOnWriteArrayList<Integer> in = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Integer> out = new CopyOnWriteArrayList<>();
        final SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<>(5);
        Thread producer = new Thread(
                () -> {
                    for (int index = 0; index < 5; index++) {
                        try {
                            queue.offer(index);
                            in.add(index);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
        Thread consumer = new Thread(
                () -> {
                    for (int index = 0; index < 5; index++) {
                        try {
                            out.add(queue.poll());

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        assertThat(out).containsExactlyElementsOf(in);
    }
}