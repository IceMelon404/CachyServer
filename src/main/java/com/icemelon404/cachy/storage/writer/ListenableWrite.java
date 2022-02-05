package com.icemelon404.cachy.storage.writer;

import com.icemelon404.cachy.storage.KeyValue;


public class ListenableWrite {

    private final WriteCallBack callBack;
    private final KeyValue keyVal;

    public ListenableWrite(KeyValue keyValue, WriteCallBack callable) {
        this.keyVal = keyValue;
        this.callBack = callable;
    }

    public void notifyComplete() {
        callBack.onWriteFinish(new WriteResult(keyVal));
    }

    public void notifyError(Exception error) {
        callBack.onWriteFinish(new WriteResult(error));
    }

    public KeyValue getKeyVal() {
        return keyVal;
    }
}
