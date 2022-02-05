package com.icemelon404.cachy.network.set;

import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.reactive.ReactiveWriter;

public class SetMessageHandlerImpl implements SetMessageHandler {

    private final ReactiveWriter writer;

    public SetMessageHandlerImpl(ReactiveWriter writer) {
        this.writer = writer;
    }

    @Override
    public void handleSet(SetMessage set, Acknowledgement ack) {
        writer.write(new KeyValue(set.key, set.value)).subscribe(null, ack::nack, ack::ack);
    }
}
