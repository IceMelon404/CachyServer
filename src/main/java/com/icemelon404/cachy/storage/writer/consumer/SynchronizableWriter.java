package com.icemelon404.cachy.storage.writer.consumer;

import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.Destroyable;

public interface SynchronizableWriter extends Destroyable {
    void write(KeyValue keyValue);
    void sync();
}
