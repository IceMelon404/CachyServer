package com.icemelon404.cachy.storage.file.repository;

import com.icemelon404.cachy.storage.file.resolver.IdFileResolver;
import com.icemelon404.cachy.storage.common.Loader;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;
import com.icemelon404.cachy.storage.repository.MainSegmentRepository;
import com.icemelon404.cachy.storage.file.resolver.FileWithVersion;
import com.icemelon404.cachy.storage.file.FileWithId;
import com.icemelon404.cachy.storage.repository.MainSegmentLoadResult;

import java.util.*;
import java.util.stream.Collectors;

public class DirectoryMainSegmentRepository implements MainSegmentRepository {

    private final Loader<FileWithId, OrderedReactiveReadableSegment> fileLoader;
    private final IdFileResolver resolver;

    public DirectoryMainSegmentRepository(Loader<FileWithId, OrderedReactiveReadableSegment> fileLoader,
                                          IdFileResolver resolver) {
        this.fileLoader = fileLoader;
        this.resolver = resolver;
    }

    static class SegmentWithVersion {
        final OrderedReactiveReadableSegment segment;
        final int version;
        
        SegmentWithVersion(OrderedReactiveReadableSegment segment, int version) {
            this.segment = segment;
            this.version = version;
        }
    }
    
    @Override
    public MainSegmentLoadResult findSegments() {
        Set<Long> failedSegmentIds = new HashSet<>();
        List<SegmentWithVersion> segments = new LinkedList<>();
        Collection<FileWithVersion> fileWithIds = resolver.getFileWithIds();
        for (FileWithVersion fileWithId : fileWithIds) {
            try {
                segments.add(new SegmentWithVersion(fileLoader.load(fileWithId.fileWithId()), fileWithId.version()));
            } catch (Exception e) {
                failedSegmentIds.add(fileWithId.id());
                fileWithId.file().delete();
            }
        }
        removeLoadedSegments(segments, failedSegmentIds);
        removeDuplicate(segments);
        return new MainSegmentLoadResult(failedSegmentIds, unwrap(segments));
    }
    
    private Collection<OrderedReactiveReadableSegment> unwrap(List<SegmentWithVersion> segmentWithVersions) {
        return segmentWithVersions.stream().map(it->it.segment).collect(Collectors.toSet());
    }

    private void removeLoadedSegments(Collection<SegmentWithVersion> loadedSegments, Set<Long> failedIds) {
        loadedSegments.forEach((it)->failedIds.remove(it.segment.getId()));
    }

    private void removeDuplicate(List<SegmentWithVersion> segmentList) {
        segmentList.sort((o1, o2) -> {
            int idCompare = Long.compare(o2.segment.getId(), o1.segment.getId());
            if (idCompare != 0)
                return idCompare;
            return Long.compare(o2.version, o1.version);
        });
        long lastId = -1;
        Iterator<SegmentWithVersion> it = segmentList.iterator();
        while (it.hasNext()) {
            SegmentWithVersion segment = it.next();
            if (segment.segment.getId() == lastId) {
                it.remove();
                segment.segment.destroy();
            }
            lastId = segment.segment.getId();
        }
    }
}
