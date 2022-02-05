package com.icemelon404.cachy.storage.blockingcomposite;

import com.icemelon404.cachy.storage.Identifiable;
import com.icemelon404.cachy.storage.blocking.BlockingReader;
import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;
import com.icemelon404.cachy.storage.grouped.BlockingReaderComposite;
import com.icemelon404.cachy.storage.grouped.ReadResult;

import java.util.*;

public class BlockingReaderCompositeImpl implements BlockingReaderComposite {

    private final TreeSet<OrderedBlockingReadableSegment> list = new TreeSet<>();

    @Override
    public Optional<ReadResult> read(String key) {
        for (OrderedBlockingReadableSegment reader : list) {
            byte[] val = reader.read(key);
            if (val != null)
                return Optional.of(ReadResult.success(reader.getId(), val));
        }
        return Optional.empty();
    }

    @Override
    public void remove(OrderedBlockingReadableSegment dataStore) throws NoSuchElementException {
        if (!list.remove(dataStore))
            throw new NoSuchElementException();
    }

    @Override
    public void add(OrderedBlockingReadableSegment reader) {
        list.add(reader);
    }

    @Override
    public long maxId() {
        return list.stream().mapToLong(Identifiable::getId).max().orElse(0);
    }
}
