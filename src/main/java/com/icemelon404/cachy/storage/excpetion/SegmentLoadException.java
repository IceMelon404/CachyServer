package com.icemelon404.cachy.storage.excpetion;

public class SegmentLoadException extends RuntimeException{

    public SegmentLoadException(String msg) {super(msg);}
    public SegmentLoadException(Exception e, String msg) {super(msg,e);}
}
