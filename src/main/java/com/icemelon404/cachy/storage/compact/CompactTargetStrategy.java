package com.icemelon404.cachy.storage.compact;

import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;

import java.util.Collection;
import java.util.TreeSet;

public interface CompactTargetStrategy {
    Collection<OrderedReactiveReadableSegment> getCompactTargets(TreeSet<OrderedReactiveReadableSegment> orderedSegments);
}
