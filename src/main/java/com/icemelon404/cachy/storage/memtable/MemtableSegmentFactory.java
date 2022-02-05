package com.icemelon404.cachy.storage.memtable;

import com.icemelon404.cachy.storage.propagate.SizeAwareBlockingSegment;

import java.util.function.Supplier;

public class MemtableSegmentFactory implements Supplier<SizeAwareBlockingSegment> {

    private final Supplier<Long> counter;

    public MemtableSegmentFactory(Supplier<Long> counter) {
        this.counter = counter;
    }

    @Override
    public SizeAwareBlockingSegment get() {
        return new MemtableSegment(counter.get());
    }
}
