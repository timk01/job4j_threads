package ru.job4j.cash;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Optional;

@ThreadSafe
public class AccountStorage {

    @GuardedBy("this")
    private final HashMap<Integer, Account> accounts = new HashMap<>();

    /**
     * putIfAbsent:
     * - если значения по ключу не было, добавляет новое и возвращает null;
     * - если значение уже было, ничего не меняет и возвращает предыдущее.
     * Поэтому null == успешно добавили.
     */

    public synchronized boolean add(Account account) {
        return accounts.putIfAbsent(account.id(), account) == null;
    }

    /**
     * replace:
     * - заменяет значение только если ключ уже существует;
     * - возвращает предыдущее значение, либо null, если ключа не было.
     * Поэтому != null == успешно обновили.
     */

    public synchronized boolean update(Account account) {
        return accounts.replace(account.id(), account) != null;
    }

    /**
     * просто удаляем с помощью ремув (если нулль - в плане не удалили, хрен с ним)
     *
     * @param id
     */
    public synchronized void delete(int id) {
        accounts.remove(id);
    }

    public synchronized Optional<Account> getById(int id) {
        Account account = accounts.get(id);
        return account != null ? Optional.of(account) : Optional.empty();
    }

    public synchronized boolean transfer(int fromId, int toId, int amount) {
        Optional<Account> from = getById(fromId);
        Optional<Account> to = getById(toId);
        if (from.isEmpty() || to.isEmpty() || amount <= 0) {
            return false;
        }

        Account firstAcc = from.get();
        if (firstAcc.amount() < amount) {
            return false;
        }

        update(new Account(firstAcc.id(), firstAcc.amount() - amount));

        Account secondAcc = to.get();
        update(new Account(secondAcc.id(), secondAcc.amount() + amount));

        return true;
    }
}
