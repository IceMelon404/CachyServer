package com.icemelon404.cachy.storage.threshold;

import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.reactive.ReactiveWriter;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

public class ReactiveWriterThresholdSupport implements ReactiveWriter {

    private final long threshold;
    private SizeAwareSegment dataStore;
    private final Supplier<SizeAwareSegment> factory;
    private final ThresholdEventHandler createCallBack;

    public ReactiveWriterThresholdSupport(long threshold,
                                          Supplier<SizeAwareSegment> factory,
                                          ThresholdEventHandler createCallBack) {
        this.threshold = threshold;
        this.factory = factory;
        this.createCallBack = createCallBack;
        this.dataStore = factory.get();
        createCallBack.onNewSegment(this.dataStore);
    }

    public Mono<Void> write(KeyValue keyVal) {
        Mono<Void> ret = dataStore.write(keyVal);
        handleThreshold();
        return ret;
    }

    private void handleThreshold() {
        if (dataStore.getSize() >= threshold) {
            SizeAwareSegment newDataStore = factory.get();
            SizeAwareSegment oldDataStore = dataStore;
            this.dataStore = newDataStore;
            createCallBack.onNewSegment(newDataStore);
            createCallBack.onSegmentReplace(oldDataStore, newDataStore);
        }
    }
}
