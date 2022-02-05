package com.icemelon404.cachy.storage.threshold;

public interface ThresholdEventHandler {
    void onSegmentReplace(SizeAwareSegment old, SizeAwareSegment newOne);
    void onNewSegment(SizeAwareSegment newOne);
}
