package com.icemelon404.cachy.storage.repository;

import com.icemelon404.cachy.storage.Destroyable;
import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;
import com.icemelon404.cachy.storage.excpetion.SegmentLoadException;
import com.icemelon404.cachy.storage.Identifiable;
import com.icemelon404.cachy.storage.grouped.SegmentGroupHandler;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;

import java.util.*;
import java.util.stream.Collectors;

public class FallBackSupportSegmentRepository implements SegmentRepository  {

    private final MainSegmentRepository mainSegmentRepo;
    private final RecoveredSegmentRepository fallBackSegmentRepo;

    public FallBackSupportSegmentRepository(MainSegmentRepository mainSegmentRepo,
                                            RecoveredSegmentRepository fallBackSegmentRepo) {
        this.mainSegmentRepo = mainSegmentRepo;
        this.fallBackSegmentRepo = fallBackSegmentRepo;
    }

    @Override
    public void loadSegments(SegmentGroupHandler dest) {
        MainSegmentLoadResult mainSegmentLoadResult = mainSegmentRepo.findSegments();
        Collection<OrderedBlockingReadableSegment> fallBackSegmentLoadResult = fallBackSegmentRepo.recoverSegments();
        checkRecovered(fallBackSegmentLoadResult, mainSegmentLoadResult.getFailedIds());
        addAndExportAll(filterLoadedSegments(mainSegmentLoadResult.getLoadedSegments(), fallBackSegmentLoadResult), dest);
        addAll(mainSegmentLoadResult.getLoadedSegments(), dest);
    }

    private void addAndExportAll(Collection<OrderedBlockingReadableSegment> recovered, SegmentGroupHandler dest) {
        for (OrderedBlockingReadableSegment segment : recovered) {
            dest.add(segment);
            dest.export(segment);
        }
    }

    private void addAll(Collection<OrderedReactiveReadableSegment> mainSegments, SegmentGroupHandler dest) {
        for (OrderedReactiveReadableSegment segment : mainSegments)
           dest.add(segment);
    }

    private Collection<OrderedBlockingReadableSegment> filterLoadedSegments(Collection<OrderedReactiveReadableSegment> dest, Collection<OrderedBlockingReadableSegment> fallBackSegments) {
        Set<Long> mainSegmentIds = dest.stream().map(Identifiable::getId).collect(Collectors.toSet());
        Collection<OrderedBlockingReadableSegment> ret = fallBackSegments.stream()
                .filter((it)->!mainSegmentIds.contains(it.getId()))
                .collect(Collectors.toList());
        fallBackSegments.stream().filter(it-> mainSegmentIds.contains(it.getId()))
                .forEach(Destroyable::destroy);
        return ret;
    }

    private void checkRecovered(Collection<OrderedBlockingReadableSegment> aofSegments, Collection<Long> failedIds) {
        aofSegments.forEach((it)-> failedIds.remove(it.getId()));
        if (!failedIds.isEmpty())
            throw new SegmentLoadException("데이터 유실을 감지하였습니다");
    }
}
