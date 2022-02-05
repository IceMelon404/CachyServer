package com.icemelon404.cachy.storage.blocking;

public interface BlockingReader {
    byte[] read(String key);
}
