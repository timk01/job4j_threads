package ru.job4j.synch;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ThreadSafe
public class SingleLockList<T> implements Iterable<T> {

    @GuardedBy("this")
    private final List<T> list;

    public SingleLockList(List<T> list) {
        this.list = copy(list);
    }

    public synchronized void add(T value) {
        list.add(value);
    }

    public synchronized T get(int index) {
        return list.get(index);
    }

    /**
     * если list.iterator():
     * Недостаточно сделать synchronized метод iterator().
     * iterator() защищён только на момент выдачи итератора, но не защищает дальнейший обход!
     * <p>
     * Но сам обход коллекции происходит позже, снаружи класса,
     * через вызовы hasNext() и next(). Эти вызовы уже не проходят
     * через synchronized-методы SingleLockList.
     * <p>
     * Поэтому, если вернуть iterator от внутреннего list, внешний код
     * получит доступ к внутреннему состоянию без нашего lock-а
     * (а дальнейший обход будет происходить уже без синхронизации”).
     * <p>
     * если copy(list).iterator():
     * мы не отдаём наружу iterator внутреннего list-а.
     * <p>
     * Мы создаём снимок текущего состояния коллекции и отдаём iterator уже от этого снимка.
     * Поэтому внешний код не получает доступ к внутреннему изменяемому состоянию.
     * <p>
     * Важно: сам снимок нужно создавать под тем же lock-ом, что используется в add/get,
     * иначе копирование тоже может читать list параллельно с изменением.
     * <p>
     * После создания копии дальнейшие hasNext()/next() работают уже с копией,
     * поэтому им не нужна синхронизация через SingleLockList.
     *
     * @return
     */
    @Override
    public synchronized Iterator<T> iterator() {
        return copy(list).iterator();
    }

    /**
     * new CopyOnWriteArrayList<T>(origin) - можно, но тогда не очень ясно зачем синхронейзед для эдд/гет,
     * т.к. в CopyOnWriteArrayList они уже защищены (ну и с итератором та ж тема)
     * @param origin
     * @return
     */

    public List<T> copy(List<T> origin) {
        return  new ArrayList<T>(origin);
    }
}
