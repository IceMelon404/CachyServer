package com.icemelon404.cachy.storage.file.ssl;

import com.icemelon404.cachy.storage.common.Loader;
import com.icemelon404.cachy.storage.compact.CompactWriter;
import com.icemelon404.cachy.storage.excpetion.CompactionFailException;
import com.icemelon404.cachy.storage.file.FileWithId;
import com.icemelon404.cachy.storage.file.resolver.IdFileResolver;

import java.io.IOException;

public class SslSegmentWriterLoader implements Loader<Long, CompactWriter> {

    private final IdFileResolver resolver;
    private final SslReadableSegmentLoader loader;

    public SslSegmentWriterLoader(IdFileResolver resolver, SslReadableSegmentLoader loader) {
        this.resolver = resolver;
        this.loader = loader;
    }

    @Override
    public CompactWriter load(Long id) {
        try {
            return new SslSegmentWriter(new FileWithId(id, resolver.createFileWithId(id)), loader);
        } catch (IOException exception) {
            throw new CompactionFailException("", exception);
        }
    }
}
