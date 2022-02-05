package com.icemelon404.cachy.storage.grouped;

import com.icemelon404.cachy.storage.common.Converter;
import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;
import com.icemelon404.cachy.storage.reactive.ReactiveReader;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GroupedReaderImpl implements ReactiveReader, SegmentGroupHandler {

    private final BlockingReaderComposite readWriteTarget;
    private final ReactiveReaderComposite immutableSegments;
    private final Converter<OrderedBlockingReadableSegment, OrderedReactiveReadableSegment> converter;
    private final ExecutorService service = Executors.newFixedThreadPool(5);

    public GroupedReaderImpl(BlockingReaderComposite readWriteTarget,
                             ReactiveReaderComposite immutableSegments,
                             Converter<OrderedBlockingReadableSegment, OrderedReactiveReadableSegment> converter) {
        this.readWriteTarget = readWriteTarget;
        this.immutableSegments = immutableSegments;
        this.converter = converter;
    }

    @Override
    public Mono<byte[]> read(String key) {
        synchronized (this) {
            Optional<ReadResult> result = readWriteTarget.read(key);
            return result.map(readResult -> immutableSegments.read(key, readResult.segmentId)
                    .map(it -> reduce(readResult, it))
                    .defaultIfEmpty(readResult)
                    .map(it -> it.value)).orElseGet(() -> immutableSegments.read(key)
                    .map(it -> it.value));

        }
    }

    private ReadResult reduce(ReadResult a, ReadResult b) {
        if (a.segmentId < b.segmentId)
            return b;
        return a;
    }

    @Override
    public void export(OrderedBlockingReadableSegment from) {
        service.submit(()-> {
            OrderedReactiveReadableSegment converted = converter.convert(from);
            synchronized (this) {
                readWriteTarget.remove(from);
                immutableSegments.addSegment(converted);
                from.destroy();
            }
        });
    }

    @Override
    public void add(OrderedBlockingReadableSegment newOne) {
        synchronized (this) {
            readWriteTarget.add(newOne);
        }
    }

    @Override
    public void add(OrderedReactiveReadableSegment reactiveSegment) {
        synchronized (this) {
            immutableSegments.addSegment(reactiveSegment);
        }
    }

    public long nextId() {
        synchronized (this) {
            return Math.max(readWriteTarget.maxId(), immutableSegments.maxId()) + 1;
        }
    }

}
