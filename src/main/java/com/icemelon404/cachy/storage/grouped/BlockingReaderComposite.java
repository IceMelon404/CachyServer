package com.icemelon404.cachy.storage.grouped;

import com.icemelon404.cachy.storage.blocking.BlockingReader;
import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;

import java.util.NoSuchElementException;
import java.util.Optional;

public interface BlockingReaderComposite  {
    Optional<ReadResult> read(String key);
    void remove(OrderedBlockingReadableSegment dataStore) throws NoSuchElementException;
    void add(OrderedBlockingReadableSegment dataStore);
    long maxId();
}
