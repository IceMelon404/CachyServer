package com.icemelon404.cachy.storage.file.ssl;

import com.icemelon404.cachy.storage.compact.CompactWriter;
import com.icemelon404.cachy.storage.excpetion.CompactionFailException;
import com.icemelon404.cachy.storage.excpetion.SegmentCreateFailException;
import com.icemelon404.cachy.storage.file.FileWithId;
import com.icemelon404.cachy.storage.file.block.BlockIOStrategy;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;
import com.icemelon404.cachy.storage.KeyValue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SslSegmentWriter implements CompactWriter {

    private final FileWithId info;
    private final RandomAccessFile file;
    private int dataSize;
    private final File rawFile;
    private final BlockIOStrategy strategy;
    private final SslReadableSegmentLoader loader;

    public SslSegmentWriter(FileWithId fileWithId, SslReadableSegmentLoader loader) {
        this.info = fileWithId;
        this.strategy = loader.getIoStrategy();
        this.loader = loader;
        this.rawFile = fileWithId.file;
        try {
            this.file = new RandomAccessFile(rawFile, "rw");
        } catch (Exception e) {
            throw new CompactionFailException("컴팩트 대상 파일 생성 실패",e);
        }
        try {
            initialize();
        } catch (CompactionFailException e) {
            closeFile();
            throw e;
        } catch (IOException e) {
            closeFile();
            throw new CompactionFailException("", e);
        }
    }


    private void initialize() throws IOException {
        file.seek(0);
        file.write((byte) 0);
        file.writeInt(0);
    }

    @Override
    public void write(KeyValue keyVal) {
        strategy.write(keyVal, file);
        dataSize++;
    }

    private void closeFile() {
        if (file != null) {
            try {
                file.close();
            } catch (IOException exception) {
                throw new CompactionFailException("파일을 닫을 수 없습니다");
            }
        }
    }

    @Override
    public OrderedReactiveReadableSegment finish() {
        finishFile();
        return loader.load(info);
    }

    private void finishFile() {
        try {
            markValidAndDataSize();
            file.getFD().sync();
            file.close();
        } catch (IOException exception) {
            throw new SegmentCreateFailException(exception, "세그먼트 생성에 실패하였습니다");
        }
    }

    private void markValidAndDataSize() throws IOException {
        file.seek(0);
        file.write((byte) 1);
        file.writeInt(dataSize);
    }



}
