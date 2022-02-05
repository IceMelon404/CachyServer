package com.icemelon404.cachy.network.get;

import com.icemelon404.cachy.storage.reactive.ReactiveReader;

public class GetMessageHandlerImpl implements GetMessageHandler {

    private final ReactiveReader reader;

    public GetMessageHandlerImpl(ReactiveReader reader) {
        this.reader = reader;
    }

    @Override
    public void handleGet(GetMessage message, Acknowledgement acknowledgement) {
        reader.read(message.getKey()).subscribe(acknowledgement::ack, acknowledgement::nack);
    }
}
