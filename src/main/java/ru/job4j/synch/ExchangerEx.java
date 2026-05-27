package ru.job4j.synch;

import java.util.concurrent.Exchanger;

/**
 * String received = exchanger.exchange(message);
 * То есть, а именно wait for another thread to arrive, это exchange point and transfer объект. То есть,
 * в зависимости от того, какая из нитей пришла второй, первая ее будет ждать.
 * И только тогда будет срабатывать exchanger exchange.
 * <p>
 * Thread-0 отправляет: Message from first thread
 * Thread-1 отправляет: Message from second thread
 * Thread-1 получил: Message from first thread
 * Thread-0 получил: Message from second thread
 */
public class ExchangerEx {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        Thread first = new Thread(() -> {
            try {
                String message = "Message from first thread";

                System.out.println(Thread.currentThread().getName()
                        + " отправляет: " + message);

                String received = exchanger.exchange(message);

                System.out.println(Thread.currentThread().getName()
                        + " получил: " + received);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread second = new Thread(() -> {
            try {
                String message = "Message from second thread";

                System.out.println(Thread.currentThread().getName()
                        + " отправляет: " + message);

                String received = exchanger.exchange(message);

                System.out.println(Thread.currentThread().getName()
                        + " получил: " + received);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        first.start();
        second.start();
    }
}