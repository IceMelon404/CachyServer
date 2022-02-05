package com.icemelon404.cachy.storage.excpetion;

public class SegmentWriteException extends RuntimeException{

    public SegmentWriteException(String msg) {
        super(msg);
    }

    public SegmentWriteException(Exception e, String msg) {super(msg, e);}
}
