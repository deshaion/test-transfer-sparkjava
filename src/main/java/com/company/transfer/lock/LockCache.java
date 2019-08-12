package com.company.transfer.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LockCache {
    private static final ConcurrentMap<Long, Lock> map = new ConcurrentHashMap<>();


    static Lock get(long key) {
        return map.computeIfAbsent(key, k -> new ReentrantLock());
    }
}
