package com.icemelon404.cachy.storage.memtable;

import com.icemelon404.cachy.storage.AbstractIdComparable;
import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.propagate.SizeAwareBlockingSegment;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class MemtableSegment extends AbstractIdComparable implements SizeAwareBlockingSegment {

    private final TreeMap<String, byte[]> treeMap = new TreeMap<>();
    private final long id;

    public MemtableSegment(long id) {
        this.id = id;
    }

    @Override
    public Iterator<KeyValue> orderedKeyValueIterator() {
        return new Iterator<KeyValue>() {
            final Iterator<String> keyIterator = treeMap.navigableKeySet().iterator();

            @Override
            public boolean hasNext() {
                return keyIterator.hasNext();
            }

            @Override
            public KeyValue next() {
                String key = keyIterator.next();
                return new KeyValue(key, treeMap.get(key));
            }
        };
    }

    @Override
    public byte[] read(String key) {
        return treeMap.get(key);
    }

    @Override
    public void write(KeyValue keyVal) {
        treeMap.put(keyVal.key, keyVal.value);
    }

    @Override
    public int getSize() {
        return treeMap.size();
    }

    @Override
    public long getId() {
        return this.id;
    }

}
