package com.icemelon404.cachy.storage.file.ssl;

import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;
import com.icemelon404.cachy.storage.common.Converter;
import com.icemelon404.cachy.storage.excpetion.SegmentWriteException;
import com.icemelon404.cachy.storage.file.FileWithId;
import com.icemelon404.cachy.storage.file.resolver.IdFileResolver;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;

import java.io.IOException;
import java.util.Iterator;

public class SslConverter implements Converter<OrderedBlockingReadableSegment, OrderedReactiveReadableSegment> {

    private final IdFileResolver resolver;
    private final SslReadableSegmentLoader loader;

    public SslConverter(IdFileResolver resolver, SslReadableSegmentLoader loader) {
        this.resolver = resolver;
        this.loader = loader;
    }

    @Override
    public OrderedReactiveReadableSegment convert(OrderedBlockingReadableSegment segment) {
        Iterator<KeyValue> it = segment.orderedKeyValueIterator();
        try {
            FileWithId fileWithId = new FileWithId(segment.getId(), resolver.createFileWithId(segment.getId()));
            SslSegmentWriter writer = new SslSegmentWriter(fileWithId, loader);
            while (it.hasNext()) {
                writer.write(it.next());
            }
            return writer.finish();
        } catch (IOException exception) {
            throw new SegmentWriteException(exception, "memtable -> ssl 쓰기 실패");
        }
    }
}
