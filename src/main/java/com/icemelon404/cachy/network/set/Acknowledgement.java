package com.icemelon404.cachy.network.set;

public interface Acknowledgement {
    void ack();
    void nack(Throwable error);
}
