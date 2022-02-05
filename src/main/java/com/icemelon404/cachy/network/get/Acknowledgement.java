package com.icemelon404.cachy.network.get;

public interface Acknowledgement {
    void ack(byte[] value);
    void nack(Throwable exception);
}
