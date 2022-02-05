package com.icemelon404.cachy.network.set;

public interface SetMessageHandler {
    void handleSet(SetMessage set, Acknowledgement ack);
}
