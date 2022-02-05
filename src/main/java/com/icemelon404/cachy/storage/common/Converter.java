package com.icemelon404.cachy.storage.common;

public interface Converter<T, K> {
    public K convert(T t);
}
