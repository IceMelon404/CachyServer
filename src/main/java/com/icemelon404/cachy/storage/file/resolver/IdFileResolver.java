package com.icemelon404.cachy.storage.file.resolver;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface IdFileResolver {
    Collection<FileWithVersion> getFileWithIds();
    File createFileWithId(long newSegmentId) throws IOException;
    List<File> getFileWithId(long segmentId);
}
