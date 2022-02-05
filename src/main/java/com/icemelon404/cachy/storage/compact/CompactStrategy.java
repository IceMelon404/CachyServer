package com.icemelon404.cachy.storage.compact;

import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;

import java.util.Collection;

public interface CompactStrategy {
    OrderedReactiveReadableSegment compact(Collection<OrderedReactiveReadableSegment> compactTargets, CompactWriter compactDest);
}
