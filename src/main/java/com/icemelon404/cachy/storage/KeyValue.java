package com.icemelon404.cachy.storage;

import java.util.Arrays;
import java.util.Objects;

public class KeyValue {
    public String key;
    public byte[] value;

    public KeyValue(String key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        KeyValue keyValue = (KeyValue) o;
        return Objects.equals(key, keyValue.key) && Arrays.equals(value, keyValue.value);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(key);
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }
}
