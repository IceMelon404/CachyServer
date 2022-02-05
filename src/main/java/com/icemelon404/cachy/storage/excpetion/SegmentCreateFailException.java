package com.icemelon404.cachy.storage.excpetion;

public class SegmentCreateFailException extends RuntimeException{

    public SegmentCreateFailException(String msg) {
        super(msg);
    }

    public SegmentCreateFailException(Exception e, String msg) {super(msg, e);}
}
