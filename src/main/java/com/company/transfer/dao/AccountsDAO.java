package com.company.transfer.dao;

import com.company.transfer.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountsDAO {
    Account insert(Account account);

    List<Account> getAll();

    Optional<Account> getAccount(long accountId);

    void hardDeleteAll();
}
