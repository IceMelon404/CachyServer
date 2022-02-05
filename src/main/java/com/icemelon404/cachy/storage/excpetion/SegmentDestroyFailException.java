package com.icemelon404.cachy.storage.excpetion;

public class SegmentDestroyFailException extends RuntimeException {
    public SegmentDestroyFailException(String msg) {
        super(msg);
    }
    public SegmentDestroyFailException(Exception e) {super(e);}
}
