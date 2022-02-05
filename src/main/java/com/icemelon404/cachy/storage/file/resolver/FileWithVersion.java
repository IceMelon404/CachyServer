package com.icemelon404.cachy.storage.file.resolver;

import com.icemelon404.cachy.storage.file.FileWithId;

import java.io.File;

public class FileWithVersion {
    private final FileWithId fileWithId;
    private final int version;

    public FileWithVersion(FileWithId fileWithId, int version) {
        this.fileWithId = fileWithId;
        this.version = version;
    }

    public FileWithId fileWithId() {
        return fileWithId;
    }

    public File file() {
        return fileWithId.file;
    }

    public long id() {
        return fileWithId.segmentId;
    }

    public int version() {
        return this.version;
    }
}
