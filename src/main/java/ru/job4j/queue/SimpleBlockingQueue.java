package ru.job4j.queue;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;
import java.util.Queue;

/**
 * оба метода синхронизированы по this. this = объект SimpleBlockingQueue
 * <p>
 * мы когда создаем объект уже класса, инициализируем его количеством. именно тем, с кем будем сравниать размер очереди
 * <p>
 * у нас 2 блокировки: сверху, т.е. нельзя более пложить элементы и снизу - достать (чаща переполнена и пуста)
 * <p>
 * соответственно "сверху" - нельзя более положить - ожидай (и только когда разбудят, т.е. уже МОЖНО будет положить)
 * - проснись. проснулся ? ложим элемент и сообщаем всем остальным об этом (т.е. будить остальные спящи потоки:
 * "эй ряебята, уже можно действовать (а вот КАКОЕ действие - зависит от размеров чаши)"
 * снизу - наоборот: можно доставать элементы, но до теех пор пока есть что достать. уперся в низ - жди.
 *
 * @param <T>
 */

@ThreadSafe
public class SimpleBlockingQueue<T> {

    @GuardedBy("this")
    private final Queue<T> queue = new LinkedList<>();

    private final int size;

    public SimpleBlockingQueue(int size) {
        this.size = size;
    }

    public void offer(T value) throws InterruptedException {
        synchronized (this) {
            while (this.queue.size() == this.size) {
                wait();
            }
            this.queue.add(value);
            notifyAll();
        }
    }

    public T poll() throws InterruptedException {
        synchronized (this) {
            while (this.queue.isEmpty()) {
                wait();
            }
            T polled = this.queue.poll();
            notifyAll();
            return polled;
        }
    }
}