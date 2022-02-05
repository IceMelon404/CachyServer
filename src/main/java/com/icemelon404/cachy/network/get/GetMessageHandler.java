package com.icemelon404.cachy.network.get;

public interface GetMessageHandler {
    void handleGet(GetMessage message, Acknowledgement acknowledgement);
}
