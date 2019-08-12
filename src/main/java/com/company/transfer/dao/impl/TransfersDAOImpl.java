package com.company.transfer.dao.impl;

import com.company.transfer.dao.TransfersDAO;
import com.company.transfer.model.Account;
import com.company.transfer.model.Transfer;
import org.sql2o.Connection;
import org.sql2o.ResultSetHandler;
import org.sql2o.Sql2o;

import java.util.List;

public class TransfersDAOImpl implements TransfersDAO {
    private Sql2o sql2o;

    public TransfersDAOImpl(Sql2o sql2o) {
        this.sql2o = sql2o;

        initTables();
    }

    private static final ResultSetHandler<Transfer> transferResultSetHandler = resultSet ->
            Transfer.builder()
                    .id(resultSet.getLong("transferId"))
                    .requestId(resultSet.getString("requestId"))
                    .sourceAccountId(resultSet.getLong("sourceAccountId"))
                    .targetAccountId(resultSet.getLong("targetAccountId"))
                    .amount(resultSet.getBigDecimal("amount"))
                    .created(resultSet.getTimestamp("created").toLocalDateTime())
                    .build();


    private void initTables() {
        final String createTransfer = "CREATE TABLE IF NOT EXISTS Transfer\n" +
                "(\n" +
                "    transferId INT PRIMARY KEY IDENTITY,\n" +
                "    requestId VARCHAR(64) NOT NULL,\n" +
                "    sourceAccountId INT,\n" +
                "    targetAccountId INT,\n" +
                "    amount DECIMAL(10,2) DEFAULT 0 NOT NULL,\n" +
                "    created TIMESTAMP DEFAULT NOW() NOT NULL,\n" +
                "    CONSTRAINT Transfers_ACCOUNTS_ID_fk_source FOREIGN KEY (sourceAccountId) REFERENCES account (accountId) ON DELETE CASCADE,\n" +
                "    CONSTRAINT Transfers_ACCOUNTS_ID_fk_target FOREIGN KEY (targetAccountId) REFERENCES account (accountId) ON DELETE CASCADE\n" +
                ");";
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery(createTransfer).executeUpdate();
            conn.commit();
        }
    }

    @Override
    public Transfer insert(Transfer transfer, Account updatedSourceAccount, Account updatedTargetAccount) {
        try (Connection conn = sql2o.beginTransaction()) {
            conn.createQuery("INSERT INTO Transfer (transferId, requestId, sourceAccountId, targetAccountId, amount, created) " +
                    "VALUES (NULL, :requestId, :sourceAccountId, :targetAccountId, :amount, NOW())", true)
                    .addParameter("requestId", transfer.getRequestId())
                    .addParameter("sourceAccountId", transfer.getSourceAccountId())
                    .addParameter("targetAccountId", transfer.getTargetAccountId())
                    .addParameter("amount", transfer.getAmount())
                    .executeUpdate();
            long key = conn.getKey(Long.class);

            conn.createQuery("UPDATE AccountBalance SET balance = :balance WHERE accountId = :accountId", true)
                    .addParameter("accountId", updatedSourceAccount.getId())
                    .addParameter("balance", updatedSourceAccount.getBalance())
                    .executeUpdate();

            conn.createQuery("UPDATE AccountBalance SET balance = :balance WHERE accountId = :accountId", true)
                    .addParameter("accountId", updatedTargetAccount.getId())
                    .addParameter("balance", updatedTargetAccount.getBalance())
                    .executeUpdate();

            Transfer newTransfer = conn.createQuery("SELECT * FROM Transfer WHERE transferId = :id")
                    .addParameter("id", key)
                    .executeAndFetchFirst(transferResultSetHandler);
            conn.commit();
            return newTransfer;
        }
    }

    @Override
    public List<Transfer> getAll() {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("SELECT * FROM Transfer")
                    .executeAndFetch(transferResultSetHandler);
        }
    }

    @Override
    public boolean isTransferExist(String requestId) {
        try (Connection conn = sql2o.open()) {
            return conn.createQuery("SELECT COUNT(transferId) FROM Transfer WHERE requestId = :requestId")
                    .addParameter("requestId", requestId)
                    .executeAndFetchFirst(Boolean.class);
        }
    }

    @Override
    public void hardDeleteAll() {
        try (Connection conn = sql2o.open()) {
            conn.createQuery("DELETE from Transfer").executeUpdate();
        }
    }
}
