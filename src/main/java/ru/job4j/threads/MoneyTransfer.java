package ru.job4j.threads;


public class MoneyTransfer<Amount> {
    public void moneyTransferProblematic(Account from, Account to, Amount amount) throws NotEnoughFundsException {
        synchronized (from) {
            synchronized (to) {
                if (from.getBalance().compareTo(amount) < 0) {
                    throw new NotEnoughFundsException();
                } else {
                    from.debit(amount);
                    to.credit(amount);
                }
            }
        }
    }

    /**
     * всегда захватывать аккаунты в одном порядке, например по id
     * @param from
     * @param to
     * @param amount
     * @throws NotEnoughFundsException
     */

    public void moneyTransfer(Account from, Account to, Amount amount)
            throws NotEnoughFundsException {

        if (from == to) {
            return;
        }

        Account first = from.getId() < to.getId() ? from : to;
        Account second = from.getId() < to.getId() ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.getBalance().compareTo(amount) < 0) {
                    throw new NotEnoughFundsException();
                }
                from.debit(amount);
                to.credit(amount);
            }
        }
    }
}
