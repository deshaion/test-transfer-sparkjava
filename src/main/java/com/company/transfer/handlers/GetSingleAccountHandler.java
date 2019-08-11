package com.company.transfer.handlers;

import com.company.transfer.dao.AccountsDAO;
import com.company.transfer.handlers.common.AbstractRequestHandler;
import com.company.transfer.handlers.common.Answer;
import com.company.transfer.model.Account;
import com.company.transfer.model.common.Empty;

import java.util.Map;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class GetSingleAccountHandler extends AbstractRequestHandler<Empty> {
    private AccountsDAO accountsDAO;

    public GetSingleAccountHandler(AccountsDAO accountsDAO) {
        super(Empty.class);

        this.accountsDAO = accountsDAO;
    }

    @Override
    public Answer process(Empty value, Map<String, String> urlParams) {
        if (!urlParams.containsKey(":id")) {
            throw new IllegalArgumentException();
        }

        long id = Long.valueOf(urlParams.get(":id"));

        Optional<Account> account = accountsDAO.getAccount(id);

        if (!account.isPresent()) {
            return new Answer(HTTP_NOT_FOUND, account);
        }

        return Answer.ok(account);
    }
}
