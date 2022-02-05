package com.icemelon404.cachy.storage.threshold;

import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;
import com.icemelon404.cachy.storage.reactive.ReactiveWriter;
import reactor.core.publisher.Mono;

public interface SizeAwareSegment extends OrderedBlockingReadableSegment, ReactiveWriter {
    int getSize();
    Mono<Void> write(KeyValue keyValue);
}