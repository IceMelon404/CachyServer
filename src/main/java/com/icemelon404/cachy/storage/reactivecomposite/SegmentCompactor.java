package com.icemelon404.cachy.storage.reactivecomposite;

import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;

import java.util.TreeSet;

public interface SegmentCompactor {
    CompactResult compact(TreeSet<OrderedReactiveReadableSegment> compactTargets);
}
