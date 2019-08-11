package com.company.transfer.lock;

public interface TransferLock extends AutoCloseable {
    void lock();
}
