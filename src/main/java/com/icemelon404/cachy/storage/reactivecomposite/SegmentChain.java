package com.icemelon404.cachy.storage.reactivecomposite;

import com.icemelon404.cachy.storage.Identifiable;
import com.icemelon404.cachy.storage.grouped.ReactiveReaderComposite;
import com.icemelon404.cachy.storage.grouped.ReadResult;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;
import com.icemelon404.cachy.storage.reactive.ReactiveReader;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class SegmentChain implements ReactiveReaderComposite {

    private final TreeSet<OrderedReactiveReadableSegment> readableSegments = new TreeSet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final PurgeHandler purgeHandler;

    public SegmentChain(PurgeHandler purgeHandler) {
        this.purgeHandler = purgeHandler;
    }

    @Override
    public Mono<ReadResult> read(String key) {
        lock.readLock().lock();
        try {
            return readFrom(copyOf(readableSegments), key);
        } finally {
            lock.readLock().unlock();
        }
    }

    private Mono<ReadResult> readFrom(Collection<OrderedReactiveReadableSegment> readTargets, String key) {
        Mono<ReadResult> res = Mono.empty();
        purgeHandler.markOnRead(readTargets);
        for (OrderedReactiveReadableSegment segment : readTargets)
            res = res.switchIfEmpty(segment.read(key).map(it -> ReadResult.success(segment.getId(), it)));
        res = res.doOnTerminate(() -> purgeHandler.markReadDone(readTargets));
        return res;
    }

    @Override
    public Mono<ReadResult> read(String key, long segmentIdLowerBound) {
        lock.readLock().lock();
        try {
            return readFrom(filterIdBigger(readableSegments, segmentIdLowerBound), key);
        } finally {
            lock.readLock().unlock();
        }
    }

    private Collection<OrderedReactiveReadableSegment> filterIdBigger(Collection<OrderedReactiveReadableSegment> readableSegments,
                                                                      long lowerBound) {
       return readableSegments.stream().filter(it -> it.getId() > lowerBound).collect(Collectors.toSet());
    }

    @Override
    public long maxId() {
        lock.readLock().lock();
        try {
            Optional<OrderedReactiveReadableSegment> optionalSegment =
                    readableSegments.stream().min(Comparator.naturalOrder());
            return optionalSegment.map(Identifiable::getId).orElse(0L);
        } finally {
            lock.readLock().unlock();
        }
    }


    @Override
    public void addSegment(OrderedReactiveReadableSegment segment) {
        lock.writeLock().lock();
        readableSegments.add(segment);
        lock.writeLock().unlock();
    }

    public void compact(SegmentCompactor compactor) {
        CompactResult result = compactor.compact(copyOf(readableSegments));
        replaceCompacted(result);
        destroyCompacted(result);
    }

    private TreeSet<OrderedReactiveReadableSegment> copyOf(TreeSet<OrderedReactiveReadableSegment> segments) {
        lock.readLock().lock();
        TreeSet<OrderedReactiveReadableSegment> ret = new TreeSet<>(segments);
        lock.readLock().unlock();
        return ret;
    }

    private void replaceCompacted(CompactResult result) {
        lock.writeLock().lock();
        try {
            readableSegments.removeAll(result.compactedTargets);
            readableSegments.add(result.compactResult);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void destroyCompacted(CompactResult result) {
        purgeHandler.requestPurge(result.compactedTargets);
    }
}
