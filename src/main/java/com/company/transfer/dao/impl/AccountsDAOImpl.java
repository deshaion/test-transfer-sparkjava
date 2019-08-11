package com.company.transfer.dao.impl;

import com.company.transfer.dao.AccountsDAO;
import com.company.transfer.model.Account;
import org.sql2o.Connection;
import org.sql2o.ResultSetHandler;
import org.sql2o.Sql2o;

import java.util.List;
import java.util.Optional;

public class AccountsDAOImpl implements AccountsDAO {
    private Sql2o sql2o;

    public AccountsDAOImpl(Sql2o sql2o) {
        this.sql2o = sql2o;

        initTables();
    }

    private void initTables() {
        final String createAccount = "CREATE TABLE IF NOT EXISTS Account\n" +
                "(\n" +
                "    accountId INT PRIMARY KEY NOT NULL IDENTITY,\n" +
                "    name VARCHAR(255),\n" +
                "    active BOOLEAN,\n" +
                "    created TIMESTAMP DEFAULT now NOT NULL\n" +
                ")";
        final String createAccountBalance = "CREATE TABLE IF NOT EXISTS AccountBalance\n" +
                "(\n" +
                "    accountBalanceId INT PRIMARY KEY IDENTITY,\n" +
                "    accountId INT,\n" +
                "    balance DECIMAL(10,2),\n" +
                "    updated TIMESTAMP,\n" +
                "    CONSTRAINT AccountBalances_ACCOUNTS_ID_fk FOREIGN KEY (accountId) REFERENCES account (accountId) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ");";
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(createAccount).executeUpdate();
            conn.createQuery(createAccountBalance).executeUpdate();
            conn.commit();
        }
    }

    private static final ResultSetHandler<Account> accountResultSetHandler = resultSet ->
        Account.builder()
                .name(resultSet.getString("name"))
                .id(resultSet.getLong("accountId"))
                .created(resultSet.getTimestamp("created").toLocalDateTime())
                .active(resultSet.getInt("active") == 1)
                .balance(resultSet.getBigDecimal("balance"))
                .build();

    @Override
    public Account insert(Account account) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery("INSERT INTO Account (accountId, name, active, created) VALUES (NULL, :name, 1, NOW())", true)
                    .addParameter("name", account.getName())
                    .executeUpdate();
            long key = conn.getKey(Long.class);
            conn.createQuery("INSERT INTO AccountBalance (accountBalanceId, accountId, balance, updated) VALUES (NULL, :accountId, :balance, NOW())", true)
                    .addParameter("accountId", key)
                    .addParameter("balance", account.getBalance())
                    .executeUpdate();
            Account newAccount = conn.createQuery("SELECT accountId, name, active, created, accountbalance.balance FROM Account LEFT JOIN AccountBalance ON account.accountId = accountbalance.accountId WHERE accountId = :id")
                    .addParameter("id", key)
                    .executeAndFetchFirst(accountResultSetHandler);
            conn.commit();
            return newAccount;
        }
    }

    @Override
    public List<Account> getAll() {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("SELECT accountId, name, active, created, AccountBalance.balance FROM Account " +
                    "LEFT JOIN AccountBalance ON account.accountId = accountbalance.accountId " +
                    "WHERE active = 1 ")
                    .executeAndFetch(accountResultSetHandler);
        }
    }

    @Override
    public Optional<Account> getAccount(long accountId) {
        try (Connection conn = sql2o.open()) {
            return Optional.ofNullable(conn.createQuery("" +
                    "SELECT accountId, name, active, created, AccountBalance.balance FROM Account " +
                    "LEFT JOIN AccountBalance ON account.accountId = accountbalance.accountId " +
                    "WHERE Account.accountId = :accountId AND active = 1 ")

                    .addParameter("accountId", accountId)
                    .executeAndFetchFirst(accountResultSetHandler)
            );
        }
    }

    @Override
    public void hardDeleteAll() {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery("DELETE from AccountBalance").executeUpdate();
            conn.createQuery("DELETE from Account").executeUpdate();
            conn.commit();
        }
    }
}
