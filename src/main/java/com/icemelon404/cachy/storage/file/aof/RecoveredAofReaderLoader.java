package com.icemelon404.cachy.storage.file.aof;

import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;
import com.icemelon404.cachy.storage.file.FileWithId;
import com.icemelon404.cachy.storage.file.block.BlockIOStrategy;
import com.icemelon404.cachy.storage.common.Loader;

public class RecoveredAofReaderLoader implements Loader<FileWithId, OrderedBlockingReadableSegment> {

    private final BlockIOStrategy strategy;

    public RecoveredAofReaderLoader(BlockIOStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public OrderedBlockingReadableSegment load(FileWithId id) {
        return new RecoveredAofReader(id.segmentId, id.file, strategy);
    }
}
