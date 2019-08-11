package com.company.transfer.dao;

import com.company.transfer.model.Account;

import java.util.List;

public interface AccountsDAO {
    Account insert(Account account);

    List<Account> getAll();

    void deleteAll();
}
