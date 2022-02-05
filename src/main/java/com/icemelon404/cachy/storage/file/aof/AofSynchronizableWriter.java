package com.icemelon404.cachy.storage.file.aof;

import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.excpetion.SegmentDestroyFailException;
import com.icemelon404.cachy.storage.excpetion.SegmentWriteException;
import com.icemelon404.cachy.storage.file.block.BlockIOStrategy;
import com.icemelon404.cachy.storage.writer.consumer.SynchronizableWriter;

import java.io.File;
import java.io.RandomAccessFile;

public class AofSynchronizableWriter implements SynchronizableWriter {

    private RandomAccessFile file;
    private BlockIOStrategy strategy;
    private File rawFile;
    private boolean isDestroyed;

    public AofSynchronizableWriter(File file, BlockIOStrategy strategy) {
        try {
            this.rawFile = file;
            this.strategy = strategy;
            this.file = new RandomAccessFile(file, "rw");
            this.file.seek(0);
        } catch (Exception e){

        }
    }

    @Override
    public void destroy() {
        try {
            synchronized (this) {
                file.close();
                rawFile.delete();
                isDestroyed = true;
            }
        } catch (Exception e) {
            throw new SegmentDestroyFailException(e);
        }
    }

    @Override
    public void write(KeyValue keyValue) {
        strategy.write(keyValue, file);
    }

    @Override
    public void sync() {
         try {
             file.getFD().sync();
         } catch (Exception e) {
             throw new SegmentWriteException(e, "fsync 실패");
         }
    }

}
