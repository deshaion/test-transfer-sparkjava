package com.company.transfer.handlers;

import com.company.transfer.dao.AccountsDAO;
import com.company.transfer.handlers.common.AbstractRequestHandler;
import com.company.transfer.handlers.common.Answer;
import com.company.transfer.model.Empty;

import java.util.Map;

public class GetAccountsHandler extends AbstractRequestHandler<Empty> {
    private AccountsDAO accountsDAO;

    public GetAccountsHandler(AccountsDAO accountsDAO) {
        super(Empty.class);

        this.accountsDAO = accountsDAO;
    }

    @Override
    public Answer process(Empty value, Map<String, String> urlParams) {
        return Answer.ok(dataToJson(accountsDAO.getAll()));
    }
}
