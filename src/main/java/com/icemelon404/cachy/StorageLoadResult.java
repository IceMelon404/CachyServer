package com.icemelon404.cachy;

import com.icemelon404.cachy.storage.reactive.ReactiveReader;
import com.icemelon404.cachy.storage.reactive.ReactiveWriter;

public class StorageLoadResult {
    public final ReactiveReader reader;
    public final ReactiveWriter writer;

    public StorageLoadResult(ReactiveReader reader, ReactiveWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }
}
