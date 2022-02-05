package com.icemelon404.cachy.storage.reactivecomposite;

import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;

import java.util.Collection;

public class CompactResult {
    public Collection<OrderedReactiveReadableSegment> compactedTargets;
    public OrderedReactiveReadableSegment compactResult;

    public CompactResult(Collection<OrderedReactiveReadableSegment> compactedTargets, OrderedReactiveReadableSegment compactResult) {
        this.compactedTargets = compactedTargets;
        this.compactResult = compactResult;
    }
}
