package com.icemelon404.cachy.storage.writer;

import com.icemelon404.cachy.storage.propagate.LogWriter;
import com.icemelon404.cachy.storage.common.Loader;
import com.icemelon404.cachy.storage.writer.consumer.SynchronizableWriter;
import com.icemelon404.cachy.storage.writer.consumer.WriteBufferConsumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BufferingWritableSegmentLoader implements Loader<Long, LogWriter> {

    private final Loader<Long, SynchronizableWriter> loader;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public BufferingWritableSegmentLoader(Loader<Long, SynchronizableWriter> loader) {
        this.loader = loader;
    }

    @Override
    public LogWriter load(Long id) {
        WriteBufferConsumer consumer = new WriteBufferConsumer(loader.load(id), 15, 2000);
        executorService.submit(consumer);
        return new BufferingWritableSegment(consumer);
    }
}
