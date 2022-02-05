package com.icemelon404.cachy.storage;

import java.util.Iterator;

public interface OrderedReadable {
    Iterator<KeyValue> orderedKeyValueIterator();
}
