package com.icemelon404.cachy.storage.threshold;

import com.icemelon404.cachy.storage.grouped.SegmentGroupHandler;

public class ExportStrategy implements ThresholdEventHandler {

    private final SegmentGroupHandler dest;

    public ExportStrategy(SegmentGroupHandler dest) {
        this.dest = dest;
    }

    @Override
    public void onSegmentReplace(SizeAwareSegment oldDataStore, SizeAwareSegment newDataStore) {
        dest.export(oldDataStore);
    }

    @Override
    public void onNewSegment(SizeAwareSegment newOne) {
        dest.add(newOne);
    }
}
