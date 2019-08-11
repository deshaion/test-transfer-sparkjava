package com.company.transfer.dao;

import com.company.transfer.model.Account;
import com.company.transfer.model.Transfer;

import java.util.List;

public interface TransfersDAO {
    Transfer insert(Transfer transfer, Account updatedSourceAccount, Account updatedTargetAccount);

    List<Transfer> getAll();

    boolean isTransferExist(String requestId);

    void hardDeleteAll();
}
