package com.icemelon404.cachy.storage.blocking;

import com.icemelon404.cachy.storage.KeyValue;

public interface BlockingWriter {
    void write(KeyValue keyValue);
}
