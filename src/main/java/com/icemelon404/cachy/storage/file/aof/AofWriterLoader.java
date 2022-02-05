package com.icemelon404.cachy.storage.file.aof;

import com.icemelon404.cachy.storage.excpetion.SegmentCreateFailException;
import com.icemelon404.cachy.storage.file.block.BlockIOStrategy;
import com.icemelon404.cachy.storage.file.resolver.IdFileResolver;
import com.icemelon404.cachy.storage.common.Loader;
import com.icemelon404.cachy.storage.writer.consumer.SynchronizableWriter;

import java.io.IOException;

public class AofWriterLoader implements Loader<Long, SynchronizableWriter> {

    private final IdFileResolver resolver;
    private final BlockIOStrategy strategy;

    public AofWriterLoader(IdFileResolver resolver, BlockIOStrategy strategy) {
        this.resolver = resolver;
        this.strategy = strategy;
    }

    @Override
    public SynchronizableWriter load(Long id) {
        try {
            return new AofSynchronizableWriter(resolver.createFileWithId(id), strategy);
        } catch (IOException exception) {
            throw new SegmentCreateFailException(exception, "Aof 파일 생성 실패");
        }
    }
}
