package com.icemelon404.cachy.storage.reactive;

import reactor.core.publisher.Mono;

public interface ReactiveReader {
    Mono<byte[]> read(String key);
}
