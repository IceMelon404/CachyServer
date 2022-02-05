package com.icemelon404.cachy.storage.excpetion;

public class CompactionFailException extends RuntimeException{
    public CompactionFailException(String msg) {
        super(msg);
    }
    public CompactionFailException(String msg, Exception cause) {
        super(msg, cause);
    };
}
