package com.company.transfer.service;

import com.company.transfer.dao.AccountsDAO;
import com.company.transfer.dao.TransfersDAO;
import com.company.transfer.handlers.common.Answer;
import com.company.transfer.lock.TransferLock;
import com.company.transfer.lock.TransferLockImpl;
import com.company.transfer.model.Account;
import com.company.transfer.model.Transfer;
import com.company.transfer.model.common.ErrorMessage;
import lombok.Builder;

import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class TransferProcessorImpl implements TransferProcessor {
    private TransfersDAO transfersDAO;
    private AccountsDAO accountsDAO;
    private Transfer transfer;

    private Account source;
    private Account target;
    private Answer answer;

    @Override
    public Answer process() throws Exception {
        try (TransferLock lock = new TransferLockImpl(transfer)) {
            if (!findSource() || !findTarget()) {
                return answer;
            }

            if (!decreaseSourceBalance()) {
                return answer;
            }

            if (!increaseTargetBalance()) {
                return answer;
            }

            return Answer.created(transfersDAO.insert(transfer, source, target));
        }
    }

    private boolean decreaseSourceBalance() {
        if (transfer.getAmount().compareTo(source.getBalance()) > 0) {
            answer = new Answer(HTTP_BAD_REQUEST, ErrorMessage.errorBuilder()
                    .errorCode("NotEnoughMoney")
                    .errorMessage("Account " + source.getId() + " has no enough money on the balance")
                    .build()
            );

            return false;
        }

        source = Account.builder()
                .id(source.getId())
                .balance(source.getBalance().subtract(transfer.getAmount()))
                .build();
        return true;
    }

    private boolean increaseTargetBalance() {
        target = Account.builder()
                .id(target.getId())
                .balance(target.getBalance().add(transfer.getAmount()))
                .build();

        return true;
    }

    private boolean findSource() {
        Optional<Account> account = accountsDAO.getAccount(transfer.getSourceAccountId());
        if (!account.isPresent()) {
            answer = notFoundAccountAnswer(transfer.getSourceAccountId());
            return false;
        }

        source = account.get();
        return true;
    }

    private boolean findTarget() {
        Optional<Account> account = accountsDAO.getAccount(transfer.getTargetAccountId());
        if (!account.isPresent()) {
            answer = notFoundAccountAnswer(transfer.getTargetAccountId());
            return false;
        }

        target = account.get();
        return true;
    }

    private Answer notFoundAccountAnswer(Long accountId) {
        return new Answer(HTTP_NOT_FOUND, ErrorMessage.errorBuilder()
                                .errorCode("AccountNotFound")
                                .errorMessage("The account with ID " + accountId + " is not found.")
                                .build()
        );
    }

    @Builder
    TransferProcessorImpl(TransfersDAO transfersDAO, AccountsDAO accountsDAO, Transfer transfer) {
        this.transfersDAO = transfersDAO;
        this.accountsDAO = accountsDAO;
        this.transfer = transfer;
    }
}
