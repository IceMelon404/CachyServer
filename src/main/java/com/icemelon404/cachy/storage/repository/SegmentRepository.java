package com.icemelon404.cachy.storage.repository;

import com.icemelon404.cachy.storage.grouped.SegmentGroupHandler;

public interface SegmentRepository {
    void loadSegments(SegmentGroupHandler dest);
}
