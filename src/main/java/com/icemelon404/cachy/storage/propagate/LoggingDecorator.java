package com.icemelon404.cachy.storage.propagate;

import com.icemelon404.cachy.storage.AbstractIdComparable;
import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.threshold.SizeAwareSegment;
import reactor.core.publisher.Mono;

import java.util.Iterator;

public class LoggingDecorator extends AbstractIdComparable implements SizeAwareSegment {

    private final SizeAwareBlockingSegment dataStore;
    private final LogWriter writer;

    public LoggingDecorator(SizeAwareBlockingSegment dataStore, LogWriter writer) {
        this.dataStore = dataStore;
        this.writer = writer;
    }

    @Override
    public long getId() {
        return dataStore.getId();
    }

    @Override
    public Iterator<KeyValue> orderedKeyValueIterator() {
        return dataStore.orderedKeyValueIterator();
    }

    @Override
    public Mono<Void> write(KeyValue keyValue) {
        dataStore.write(keyValue);
        return writer.write(keyValue);
    }

    @Override
    public byte[] read(String key) {
        return dataStore.read(key);
    }

    @Override
    public int getSize() {
        return dataStore.getSize();
    }

    @Override
    public void destroy() {
        writer.destroy();
    }
}
