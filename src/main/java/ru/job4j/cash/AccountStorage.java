package ru.job4j.cash;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Optional;

@ThreadSafe
public class AccountStorage {

    @GuardedBy("this")
    private final HashMap<Integer, Account> accounts = new HashMap<>();

    public synchronized boolean add(Account account) {
        if (getById(account.id()).isPresent()) {
            return false;
        }
        accounts.put(account.id(), account);
        return true;
    }

    public synchronized boolean update(Account account) {
        accounts.put(account.id(), account);
        return true;
    }

    public synchronized void delete(int id) {
        Optional<Account> optionalAccount = getById(id);
        if (optionalAccount.isPresent()) {
            accounts.remove(optionalAccount.get().id());
        }
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
