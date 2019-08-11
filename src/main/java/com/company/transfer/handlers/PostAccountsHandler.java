package com.company.transfer.handlers;

import com.company.transfer.dao.AccountsDAO;
import com.company.transfer.handlers.common.AbstractRequestHandler;
import com.company.transfer.handlers.common.Answer;
import com.company.transfer.model.Account;

import java.util.Map;

public class PostAccountsHandler extends AbstractRequestHandler<Account> {
    private AccountsDAO accountsDAO;

    public PostAccountsHandler(AccountsDAO accountsDAO) {
        super(Account.class);

        this.accountsDAO = accountsDAO;
    }

    @Override
    public Answer process(Account value, Map<String, String> urlParams) {
        return Answer.ok(dataToJson(accountsDAO.insert(value)));
    }
}
