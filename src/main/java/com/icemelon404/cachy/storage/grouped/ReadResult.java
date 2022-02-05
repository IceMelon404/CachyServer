package com.icemelon404.cachy.storage.grouped;

public class ReadResult {
    public final Long segmentId;
    public final byte[] value;

    private ReadResult(Long segmentId, byte[] value) {
        this.segmentId = segmentId;
        this.value = value;
        assert segmentId != null;
        assert value != null;
    }


    public static ReadResult success(long segmentId, byte[] value) {
        return new ReadResult(segmentId, value);
    }
}
