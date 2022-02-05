package com.icemelon404.cachy.storage.grouped;

import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;
import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;

public interface SegmentGroupHandler {
    void export(OrderedBlockingReadableSegment target);
    void add(OrderedBlockingReadableSegment newSegment);
    void add(OrderedReactiveReadableSegment reactiveSegment);
}
