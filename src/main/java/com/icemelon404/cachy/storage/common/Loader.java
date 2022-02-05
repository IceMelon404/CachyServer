package com.icemelon404.cachy.storage.common;

public interface Loader<T, K> {
    K load(T id);
}
