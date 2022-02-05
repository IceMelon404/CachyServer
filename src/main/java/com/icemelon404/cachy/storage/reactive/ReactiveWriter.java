package com.icemelon404.cachy.storage.reactive;

import com.icemelon404.cachy.storage.KeyValue;
import reactor.core.publisher.Mono;

public interface ReactiveWriter {
    Mono<Void> write(KeyValue keyVal);
}
