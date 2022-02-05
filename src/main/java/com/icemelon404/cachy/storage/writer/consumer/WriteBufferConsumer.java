package com.icemelon404.cachy.storage.writer.consumer;

import com.icemelon404.cachy.storage.Destroyable;
import com.icemelon404.cachy.storage.writer.ListenableWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WriteBufferConsumer implements Runnable, Destroyable {

    private final static Logger logger = LoggerFactory.getLogger(WriteBufferConsumer.class);
    private final BlockingQueue<ListenableWrite> buffer = new LinkedBlockingQueue<>();
    private final Queue<ListenableWrite> writeAfterLastSync = new LinkedList<>();
    private final SynchronizableWriter writer;
    private final long timeout;
    private final int syncThreshold;
    private volatile boolean destroyFlag;
    private long lastDestroyCheck = System.currentTimeMillis();
    private final long destroyCheckInterval = 3000;

    private long lastCommit = System.currentTimeMillis();

    public WriteBufferConsumer(SynchronizableWriter writer,
                               long timeout,
                               int syncThreshold) {
        this.writer = writer;
        this.timeout = timeout;
        this.syncThreshold = syncThreshold;
    }

    public void requestWrite(ListenableWrite impl) {
        buffer.add(impl);
    }

    @Override
    public void run() {
        while (true) {
            pollAndWrite();
            if (shouldSync())
                sync();
            if (shouldDestroy()) {
                flush();
                writer.destroy();
                return;
            }
        }
    }

    private void flush() {
        for (ListenableWrite write : writeAfterLastSync)
            write.notifyComplete();
        ListenableWrite write;
        while ((write = buffer.poll()) != null)
            write.notifyComplete();
        buffer.clear();
        writeAfterLastSync.clear();
    }
    private boolean shouldDestroy() {
       if (System.currentTimeMillis() - lastDestroyCheck >= destroyCheckInterval) {
           lastDestroyCheck = System.currentTimeMillis();
           return this.destroyFlag;
       }
       return false;
    }

    private void pollAndWrite() {
        try {
            ListenableWrite write = buffer.poll(milliSecLeftForCommitTimeout(), TimeUnit.MILLISECONDS);
            tryWrite(write);
        } catch (InterruptedException e) {
            logger.warn("Write buffer 를 소비하는 도중 interrupt 가 발생하였습니다.", e);
        }
    }

    private void tryWrite(ListenableWrite write) {
        try {
            if (write != null) {
                writer.write(write.getKeyVal());
                writeAfterLastSync.add(write);
            }
        } catch (Exception e) {
            write.notifyError(e);
            logger.error("write 실패", e);
        }
    }

    private void sync() {
        writer.sync();
        for (ListenableWrite write :writeAfterLastSync)
            write.notifyComplete();
        writeAfterLastSync.clear();
        lastCommit = System.currentTimeMillis();
    }

    private boolean shouldSync() {
        boolean isCommitEmpty = writeAfterLastSync.isEmpty();
        boolean isCommitTimeout = milliSecLeftForCommitTimeout() == 0;
        boolean isSyncQueueFull = writeAfterLastSync.size() >= syncThreshold;
        return !isCommitEmpty && (isCommitTimeout || isSyncQueueFull);}

    private long milliSecLeftForCommitTimeout() {
        long left = nextCommitTimeout() - System.currentTimeMillis();
        return Math.max(0, left);
    }

    private long nextCommitTimeout() {
        return lastCommit + timeout;
    }

    @Override
    public void destroy() {
        this.destroyFlag = true;
    }
}
