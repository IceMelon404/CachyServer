package com.icemelon404.cachy.storage.repository;

import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;

import java.util.Collection;

public class MainSegmentLoadResult {
    private Collection<Long> failedIds;
    private Collection<OrderedReactiveReadableSegment> loadedSegments;

    public MainSegmentLoadResult(Collection<Long> failedIds, Collection<OrderedReactiveReadableSegment> loadedSegments) {
        this.failedIds = failedIds;
        this.loadedSegments = loadedSegments;
    }

    public Collection<Long> getFailedIds() {
        return failedIds;
    }

    public Collection<OrderedReactiveReadableSegment> getLoadedSegments() {
        return loadedSegments;
    }
}
