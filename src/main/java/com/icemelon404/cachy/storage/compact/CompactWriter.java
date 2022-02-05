package com.icemelon404.cachy.storage.compact;

import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;

public interface CompactWriter {
    void write(KeyValue value);
    OrderedReactiveReadableSegment finish();
}
