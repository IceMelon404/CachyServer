package com.icemelon404.cachy.storage.file.repository;

import com.icemelon404.cachy.storage.repository.RecoveredSegmentRepository;
import com.icemelon404.cachy.storage.file.FileWithId;
import com.icemelon404.cachy.storage.file.resolver.FileWithVersion;
import com.icemelon404.cachy.storage.file.resolver.IdFileResolver;
import com.icemelon404.cachy.storage.common.Loader;
import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DirectoryRecoveredSegmentRepository implements RecoveredSegmentRepository {

    private final Loader<FileWithId, OrderedBlockingReadableSegment> loader;
    private final IdFileResolver resolver;

    public DirectoryRecoveredSegmentRepository(Loader<FileWithId, OrderedBlockingReadableSegment> loader, IdFileResolver resolver) {
        this.loader = loader;
        this.resolver = resolver;
    }

    @Override
    public Collection<OrderedBlockingReadableSegment> recoverSegments() {
        Collection<FileWithVersion> files = resolver.getFileWithIds();
        throwIfDuplicateVersion(files);
        return unwrapAndLoad(files);
    }

    private void throwIfDuplicateVersion(Collection<FileWithVersion> files) {
        Map<Long, Integer> idWithVersionMap = new HashMap<>();
        for (FileWithVersion file : files) {
            if (idWithVersionMap.containsKey(file.id()))
                throw new IllegalStateException();
            idWithVersionMap.put(file.id(), file.version());
        }
    }

    private Collection<OrderedBlockingReadableSegment> unwrapAndLoad(Collection<FileWithVersion> files) {
        return files.stream()
                .map(FileWithVersion::fileWithId)
                .map(loader::load)
                .collect(Collectors.toSet());
    }
}
