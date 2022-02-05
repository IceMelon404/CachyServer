package com.icemelon404.cachy.storage.grouped;

import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;
import com.icemelon404.cachy.storage.reactive.ReactiveReader;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ReactiveReaderComposite {
    void addSegment(OrderedReactiveReadableSegment segment);
    long maxId();
    Mono<ReadResult> read(String key, long segmentIdLowerBound);
    Mono<ReadResult> read(String key);
}
