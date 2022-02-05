package com.icemelon404.cachy.storage.file.ssl;

import com.icemelon404.cachy.storage.file.FileWithId;
import com.icemelon404.cachy.storage.file.block.BlockIOStrategy;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;
import com.icemelon404.cachy.storage.common.Loader;

public class SslReadableSegmentLoader implements Loader<FileWithId, OrderedReactiveReadableSegment> {

    private final BlockIOStrategy ioStrategy;
    private final int blockSize;

    public SslReadableSegmentLoader(BlockIOStrategy ioStrategy, int blockSize) {
        this.ioStrategy = ioStrategy;
        this.blockSize = blockSize;
    }

    public BlockIOStrategy getIoStrategy() {
        return this.ioStrategy;
    }

    @Override
    public SslReadableSegment load(FileWithId id) {
        return new SslReadableSegment(id.segmentId, id.file, ioStrategy, blockSize);
    }
}
