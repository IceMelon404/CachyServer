package com.icemelon404.cachy.storage.propagate;

import com.icemelon404.cachy.storage.common.Loader;
import com.icemelon404.cachy.storage.threshold.SizeAwareSegment;

import java.util.function.Supplier;

public class LoggingDecoratorSupplier implements Supplier<SizeAwareSegment> {

    private final Supplier<SizeAwareBlockingSegment> supplier;
    private final Loader<Long, LogWriter> logSupplier;

    public LoggingDecoratorSupplier(Supplier<SizeAwareBlockingSegment> supplier, Loader<Long, LogWriter> logSupplier) {
        this.supplier = supplier;
        this.logSupplier = logSupplier;
    }

    @Override
    public SizeAwareSegment get() {
        SizeAwareBlockingSegment segment = supplier.get();
        return new LoggingDecorator(segment, logSupplier.load(segment.getId()));
    }
}
