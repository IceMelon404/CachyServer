package com.icemelon404.cachy.storage.memtable;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class SimpleIdCounter implements Supplier<Long> {

    private final AtomicLong atomicLong;

    public SimpleIdCounter(long start) {
        atomicLong = new AtomicLong(start);
    }

    @Override
    public Long get() {
        return atomicLong.getAndAdd(1);
    }

    public void set(long start) {atomicLong.set(start);}
}
