package com.icemelon404.cachy.network.set;

public class SetMessage {
    public final String key;
    public final byte[] value;

    public SetMessage(String key, byte[] value) {
        this.key = key;
        this.value = value;
    }
}
