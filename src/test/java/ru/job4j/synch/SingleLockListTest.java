package ru.job4j.synch;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.*;

class SingleLockListTest {

    /**
     * То есть iterator должен быть привязан не к живому внутреннему списку, а к копии!
     * (на момент создание здесь ОДИН элемент
     *
     * Если бы ты возвращал iterator от внутреннего ArrayList,
     * то после list.add(2) могло бы быть плохо: iterator был бы связан с живым списком, список изменился,
     * и при next() обычный fail-fast iterator мог бы кинуть ConcurrentModificationException.
     */
    @Test
    public void whenIt() {
        var init = new ArrayList<Integer>();
        SingleLockList<Integer> list = new SingleLockList<>(init);
        list.add(1);
        var it = list.iterator();
        list.add(2);
        assertThat(it.next()).isEqualTo(1);
    }

    @Test
    public void whenAdd() throws InterruptedException {
        var init = new ArrayList<Integer>();
        SingleLockList<Integer> list = new SingleLockList<>(init);
        Thread first = new Thread(() -> list.add(1));
        Thread second = new Thread(() -> list.add(2));
        first.start();
        second.start();
        first.join();
        second.join();
        Set<Integer> rsl = new TreeSet<>();
        list.iterator().forEachRemaining(rsl::add);
        assertThat(rsl).hasSize(2).containsAll(Set.of(1, 2));
    }

    @Test
    public void whenIteratorCreatedAfterAddThenItSeesAllElements() throws InterruptedException {
        SingleLockList<Integer> list = new SingleLockList<>(new ArrayList<>());
        list.add(1);

        Thread thread = new Thread(() -> list.add(2));
        thread.start();
        thread.join();

        Set<Integer> result = new TreeSet<>();
        list.iterator().forEachRemaining(result::add);

        assertThat(result).hasSize(2).containsAll(Set.of(1, 2));
    }

    @Test
    public void whenIteratorCreatedBeforeAddThenItDoesNotSeeNewElement() throws InterruptedException {
        SingleLockList<Integer> list = new SingleLockList<>(new ArrayList<>());
        list.add(1);

        Iterator<Integer> iterator = list.iterator();

        Thread thread = new Thread(() -> list.add(2));
        thread.start();
        thread.join();

        assertThat(iterator.next()).isEqualTo(1);
        assertThat(iterator.hasNext()).isFalse();
    }
}