package com.icemelon404.cachy.storage.excpetion;

public class SegmentReadException extends RuntimeException {
    public SegmentReadException(Exception e, String msg) {super(msg,e);}
    public SegmentReadException(String msg) {super(msg);}
}
