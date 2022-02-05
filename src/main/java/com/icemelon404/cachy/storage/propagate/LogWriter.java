package com.icemelon404.cachy.storage.propagate;

import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.Destroyable;
import reactor.core.publisher.Mono;

public interface LogWriter extends Destroyable {
    Mono<Void> write(KeyValue keyValue);
}
