package com.company.transfer.lock;

import com.company.transfer.model.Transfer;

public class TransferLockImpl implements TransferLock {
    private Transfer transfer;

    public TransferLockImpl(Transfer transfer) {
        this.transfer = transfer;
    }

    @Override
    public void lock() {

    }

    @Override
    public void close() {

    }
}
