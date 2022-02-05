package com.icemelon404.cachy;

import com.icemelon404.cachy.config.CachyConfig;
import com.icemelon404.cachy.config.ConfigKey;
import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.common.Converter;
import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;
import com.icemelon404.cachy.storage.compact.CompactWriter;
import com.icemelon404.cachy.storage.compact.MergeCompactStrategy;
import com.icemelon404.cachy.storage.compact.MostRecentCompactTargetStrategy;
import com.icemelon404.cachy.storage.compact.SegmentCompactorImpl;
import com.icemelon404.cachy.storage.file.aof.RecoveredAofReaderLoader;
import com.icemelon404.cachy.storage.file.repository.DirectoryRecoveredSegmentRepository;
import com.icemelon404.cachy.storage.file.ssl.SslConverter;
import com.icemelon404.cachy.storage.file.ssl.SslSegmentWriter;
import com.icemelon404.cachy.storage.file.ssl.SslSegmentWriterLoader;
import com.icemelon404.cachy.storage.grouped.BlockingReaderComposite;
import com.icemelon404.cachy.storage.blockingcomposite.BlockingReaderCompositeImpl;
import com.icemelon404.cachy.storage.grouped.ReactiveReaderComposite;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;
import com.icemelon404.cachy.storage.reactivecomposite.SegmentChain;
import com.icemelon404.cachy.storage.file.aof.AofWriterLoader;
import com.icemelon404.cachy.storage.file.repository.DirectoryMainSegmentRepository;
import com.icemelon404.cachy.storage.file.ssl.SslReadableSegmentLoader;
import com.icemelon404.cachy.storage.file.resolver.IdFileResolver;
import com.icemelon404.cachy.storage.grouped.GroupedReaderImpl;
import com.icemelon404.cachy.storage.memtable.SimpleIdCounter;
import com.icemelon404.cachy.storage.common.Loader;
import com.icemelon404.cachy.storage.propagate.LogWriter;
import com.icemelon404.cachy.storage.propagate.LoggingDecoratorSupplier;
import com.icemelon404.cachy.storage.propagate.SizeAwareBlockingSegment;
import com.icemelon404.cachy.storage.purge.DefaultPurgeHandler;
import com.icemelon404.cachy.storage.repository.FallBackSupportSegmentRepository;
import com.icemelon404.cachy.storage.threshold.ExportStrategy;
import com.icemelon404.cachy.storage.memtable.MemtableSegmentFactory;
import com.icemelon404.cachy.storage.file.block.DefaultBlockIOStrategy;
import com.icemelon404.cachy.storage.file.resolver.SingleDirectoryResolver;
import com.icemelon404.cachy.storage.threshold.ReactiveWriterThresholdSupport;
import com.icemelon404.cachy.storage.threshold.SizeAwareSegment;
import com.icemelon404.cachy.storage.threshold.ThresholdEventHandler;
import com.icemelon404.cachy.storage.writer.BufferingWritableSegmentLoader;
import java.io.File;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class CachyStorageBootstrap {
    private final int port;
    private final String dataPath;
    private final int blockSize;
    private final int concurrency;
    private final int maxCompactSize;
    private final int threshold;
    private final long compactInterval;

    public CachyStorageBootstrap(CachyConfig config) {
        port = (int) config.get(ConfigKey.SERVER_PORT);
        dataPath = (String) config.get(ConfigKey.DATA_PATH);
        blockSize = (int) config.get(ConfigKey.SSL_BLOCK_SIZE);
        concurrency = (int) config.get(ConfigKey.SSL_CONCORRENCY);
        maxCompactSize = (int) config.get(ConfigKey.COMPACT_MAX_SIZE);
        threshold = (int) config.get(ConfigKey.MEMTABLE_THRESHOLD);
        compactInterval = (long) config.get(ConfigKey.COMPACT_INTERVAL);
    }

    public StorageLoadResult runStorage() throws InterruptedException {
        IdFileResolver sslResolver = new SingleDirectoryResolver(dataPath + File.separator + "data");
        IdFileResolver aofResolver = new SingleDirectoryResolver(dataPath + File.separator + "aof");
        DefaultBlockIOStrategy readWriteStrategy = new DefaultBlockIOStrategy();

        BlockingReaderComposite composite = new BlockingReaderCompositeImpl();
        DefaultPurgeHandler purgeHandler = new DefaultPurgeHandler();
        SegmentChain reactiveReaderComposite = new SegmentChain(purgeHandler);

        SslReadableSegmentLoader sslLoader = new SslReadableSegmentLoader(readWriteStrategy, blockSize);
        Converter<OrderedBlockingReadableSegment, OrderedReactiveReadableSegment> sslConverter = new SslConverter(sslResolver, sslLoader);
        GroupedReaderImpl handler = new GroupedReaderImpl(composite, reactiveReaderComposite, sslConverter);

        DirectoryMainSegmentRepository mainRepo = new DirectoryMainSegmentRepository(sslLoader, sslResolver);
        DirectoryRecoveredSegmentRepository subRepo = new DirectoryRecoveredSegmentRepository(new RecoveredAofReaderLoader(readWriteStrategy), aofResolver);
        FallBackSupportSegmentRepository repo = new FallBackSupportSegmentRepository(mainRepo, subRepo);
        repo.loadSegments(handler);

        SimpleIdCounter counter = new SimpleIdCounter(handler.nextId());
        Supplier<SizeAwareBlockingSegment> memtableSupplier = new MemtableSegmentFactory(counter);
        Loader<Long, LogWriter> logLoader = new BufferingWritableSegmentLoader(new AofWriterLoader(aofResolver, readWriteStrategy));
        Supplier<SizeAwareSegment> loggingSegmentSupplier = new LoggingDecoratorSupplier(memtableSupplier, logLoader);
        ThresholdEventHandler thresholdEventHandler = new ExportStrategy(handler);
        ReactiveWriterThresholdSupport writer = new ReactiveWriterThresholdSupport(this.threshold, loggingSegmentSupplier, thresholdEventHandler);


        SegmentCompactorImpl impl = new SegmentCompactorImpl(new MostRecentCompactTargetStrategy(maxCompactSize), new MergeCompactStrategy(), new SslSegmentWriterLoader(sslResolver, sslLoader));
        Executors.newSingleThreadExecutor().submit(purgeHandler);
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() ->{ try {reactiveReaderComposite.compact(impl);} catch (Exception ignored){}}, compactInterval, compactInterval, TimeUnit.MILLISECONDS );

        return new StorageLoadResult(handler, writer);
    }
}
