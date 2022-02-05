package com.icemelon404.cachy.storage.writer;

import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.propagate.LogWriter;
import com.icemelon404.cachy.storage.writer.consumer.WriteBufferConsumer;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BufferingWritableSegment  implements LogWriter {

    private final WriteBufferConsumer buffer;

    public BufferingWritableSegment(WriteBufferConsumer buffer) {
        this.buffer = buffer;
    }

    public Mono<Void> write(KeyValue value) {
        return Mono.create(sink -> {
            ListenableWrite write = new ListenableWrite(value, result ->  {
                if (result.getError() != null)
                    sink.error(result.getError());
                else
                    sink.success();
            });
            buffer.requestWrite(write);
        });
    }

    @Override
    public void destroy() {
        buffer.destroy();
    }
}
