package com.icemelon404.cachy.storage.writer;

import com.icemelon404.cachy.storage.KeyValue;

public class WriteResult {
    private KeyValue keyValue;
    private Exception error;

    public WriteResult(KeyValue keyValue) {
        this.keyValue = keyValue;
    }
    public WriteResult(Exception error) {
        this.error = error;
    }

    public KeyValue getKeyValue() {
        return keyValue;
    }

    public Exception getError() {
        return error;
    }
}
