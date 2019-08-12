package com.company.transfer.lock;

import com.company.transfer.model.Transfer;

import java.util.concurrent.locks.Lock;

public class TransferLockImpl implements TransferLock {
    private Transfer transfer;
    private Lock firstLock;
    private Lock secondLock;

    public TransferLockImpl(Transfer transfer) {
        this.transfer = transfer;
    }

    @Override
    public void lock() {
        long firstAccount = Math.min(transfer.getSourceAccountId(), transfer.getTargetAccountId());
        long secondAccount = Math.max(transfer.getSourceAccountId(), transfer.getTargetAccountId());

        firstLock = LockCache.get(firstAccount);
        firstLock.lock();

        secondLock = LockCache.get(secondAccount);
        secondLock.lock();
    }

    @Override
    public void close() {
        firstLock.unlock();
        secondLock.unlock();
    }
}
