package com.icemelon404.cachy.storage.repository;

import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;

import java.util.Collection;

public interface RecoveredSegmentRepository {
    Collection<OrderedBlockingReadableSegment> recoverSegments();
}
