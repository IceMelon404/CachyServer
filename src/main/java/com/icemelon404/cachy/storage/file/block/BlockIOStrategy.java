package com.icemelon404.cachy.storage.file.block;

import com.icemelon404.cachy.storage.KeyValue;

import java.io.RandomAccessFile;

public interface BlockIOStrategy {
    void write(KeyValue keyValue, RandomAccessFile filePointer);
    KeyValue read(RandomAccessFile filePointer);
}
