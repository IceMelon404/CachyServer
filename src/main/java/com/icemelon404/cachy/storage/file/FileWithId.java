package com.icemelon404.cachy.storage.file;

import java.io.File;

public class FileWithId {
    public long segmentId;
    public File file;

    public FileWithId(long segmentId, File file) {
        this.segmentId = segmentId;
        this.file = file;
    }
}
