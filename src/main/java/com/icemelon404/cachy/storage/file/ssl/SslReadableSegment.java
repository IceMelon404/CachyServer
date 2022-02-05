package com.icemelon404.cachy.storage.file.ssl;

import com.icemelon404.cachy.storage.AbstractIdComparable;
import com.icemelon404.cachy.storage.excpetion.SegmentCreateFailException;
import com.icemelon404.cachy.storage.excpetion.SegmentReadException;
import com.icemelon404.cachy.storage.file.block.BlockIOStrategy;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;
import com.icemelon404.cachy.storage.KeyValue;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SslReadableSegment extends AbstractIdComparable implements OrderedReactiveReadableSegment {

    private final long id;
    private final BlockIOStrategy strategy;
    private final File rawFile;
    private static final ExecutorService executor = Executors.newFixedThreadPool(100);
    private final TreeMap<String, Long> indexMap = new TreeMap<>();
    private final int blockSize;
    private final RandomAccessFilePool filePool;
    private String upperBound;

    public SslReadableSegment(long id, File file, BlockIOStrategy ioStrategy, int blockSize) {
        this.id = id;
        this.strategy = ioStrategy;
        this.rawFile = file;
        this.blockSize = blockSize;
        try {
            filePool = new RandomAccessFilePool(rawFile, 30);
        } catch (Exception e) {
            throw new SegmentCreateFailException(e, "FilePool 생성 실패");
        }
        try  {
            RandomAccessFile check = file();
            readDataList(check, blockSize);
            if (!isValid(check))
                throw new SegmentReadException("유효한 파일이 아닙니다");
            filePool.retrieve(check);
        } catch (SegmentReadException e) {
            close();
            throw e;
        } catch (Exception e) {
            close();
            throw new SegmentReadException("파일 열기에 실패하였습니다");
        }
    }

    private RandomAccessFile file() {
        try {
            return this.filePool.get();
        } catch (InterruptedException e) {
            throw new SegmentReadException(e, "파일 풀에서 파일 반환 중 오류");
        }
    }

    @Override
    public Mono<byte[]> read(String key) {
        return Mono.create(sink ->  {
            try {
                String floorKey = indexMap.floorKey(key);
                if (floorKey == null || key.compareTo(upperBound) > 0) {
                    sink.success();
                    return;
                }
                executor.submit(() -> {
                    long blockOffset = indexMap.get(floorKey);
                    int readSize = 0;
                    RandomAccessFile file = file();
                    try {
                        file.seek(blockOffset);
                        while (readSize < blockSize) {
                            KeyValue data = strategy.read(file);
                            String dataKey = data.key;
                            if (key.equals(dataKey)) {
                                filePool.retrieve(file);
                                sink.success(data.value);
                                return;
                            }
                            readSize++;
                        }
                        filePool.retrieve(file);
                        sink.success();
                    } catch (SegmentReadException exception) {
                        filePool.retrieve(file);
                        sink.error(exception);
                    } catch (IOException e) {
                        filePool.retrieve(file);
                        sink.error(new SegmentReadException(e, "오프셋을 읽을 수 없습니다"));
                    }
                });
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }


    private void readDataList(RandomAccessFile ioFile, int blockSize) throws IOException {
        if(!readValidBit(ioFile))
            throw new SegmentReadException("유효하지 않은 세그먼트입니다");
        int dataSize = readSize(ioFile);
        int currentBlock = 0;
        for (int i = 0; i < dataSize; i++) {
            try {
                long startOffset = ioFile.getFilePointer();
                KeyValue data = strategy.read(ioFile);
                if (currentBlock <= 0) {
                    String key = data.key;
                    indexMap.put(key, startOffset);
                    currentBlock = blockSize;
                }
                int lastIndex = dataSize - 1;
                if (i == lastIndex)
                    upperBound = data.key;
                currentBlock--;
            } catch (EOFException e) {
                throw new SegmentReadException("세그먼트를 읽을 수 없습니다");
            }
        }
        if (ioFile.length() != ioFile.getFilePointer())
            throw new SegmentReadException("파일 길이가 명시된 사이즈보다 큽니다 size: " + dataSize);
    }

    public void close() {
        filePool.close();
    }

    private boolean readValidBit(RandomAccessFile file) throws IOException {
        file.seek(0);
        return file.readByte() == (byte) 1;
    }

    private boolean isValid(RandomAccessFile file) {
        try {
            return readValidBit(file);
        } catch (IOException exception) {
            return false;
        }
    }

    @Override
    public Iterator<KeyValue> orderedKeyValueIterator() {

        RandomAccessFile itFile = null;
        try {
            itFile = new RandomAccessFile(this.rawFile, "r");
        } catch (FileNotFoundException e) {
            throw new SegmentReadException(e, "");
        }
        try {
            int size = readSize(itFile);
            RandomAccessFile finalItFile = itFile;
            return new Iterator<KeyValue> () {
                int currentIdx = 0;
                @Override
                public boolean hasNext() {
                    boolean hasNext = currentIdx < size;
                    if (!hasNext) {
                        try {
                            finalItFile.close();
                        } catch (IOException exception) {
                            throw new SegmentReadException(exception, "");
                        }
                    }
                    return hasNext;
                }

                @Override
                public KeyValue next() {
                    if (!hasNext())
                        throw new SegmentReadException("더 이상 읽을 내용이 없습니다");
                    KeyValue data = strategy.read(finalItFile);
                    currentIdx++;
                    return data;
                }
            };
        } catch (IOException e) {
            try {
                itFile.close();
            } catch (IOException exception) {
                throw new SegmentReadException(exception, "");
            }
            throw new SegmentReadException(e, "사이즈 읽기 실패");
        }
    }

    private int readSize(RandomAccessFile file) throws IOException {
        file.seek(1);
        try {
            return file.readInt();
        } catch (EOFException e) {
            throw new SegmentReadException("파일 사이즈를 읽을 수 없습니다");
        }
    }

    @Override
    public void destroy() {
        close();
        rawFile.delete();
    }

    @Override
    public long getId() {
        return id;
    }

}
