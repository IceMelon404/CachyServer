package com.icemelon404.cachy.storage.file.aof;

import com.icemelon404.cachy.storage.excpetion.SegmentCreateFailException;
import com.icemelon404.cachy.storage.excpetion.SegmentDestroyFailException;
import com.icemelon404.cachy.storage.excpetion.SegmentReadException;
import com.icemelon404.cachy.storage.file.block.BlockIOStrategy;
import com.icemelon404.cachy.storage.blocking.OrderedBlockingReadableSegment;
import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.AbstractIdComparable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.TreeMap;

public class RecoveredAofReader extends AbstractIdComparable implements OrderedBlockingReadableSegment {

    private final long id;
    private BlockIOStrategy strategy;
    private File rawFile;
    private RandomAccessFile file;
    private Logger logger = LoggerFactory.getLogger(RecoveredAofReader.class);
    private final TreeMap<String, byte[]> keyValueMap = new TreeMap<>();

    public RecoveredAofReader(long id, File file, BlockIOStrategy strategy) {
        this.id = id;
        this.strategy = strategy;
        this.rawFile = file;
        try {
            this.file = new RandomAccessFile(file, "rw");
            this.file.seek(0);
        } catch (Exception e) {
            throw new SegmentCreateFailException(e, "Aof 파일 로드 실패");
        }
        try {
            loadAllDataToMap();
        } catch (Exception e) {
            logger.warn("aof 에 대한 쓰기 유실 가능성", e);
        }
    }

    private void loadAllDataToMap() throws IOException {
        while (file.getFilePointer() < file.length()) {
            KeyValue data = strategy.read(file);
            keyValueMap.put(data.key, data.value);
        }
        file.close();
    }

    @Override
    public byte[] read(String key) {
        return keyValueMap.get(key);
    }

    @Override
    public Iterator<KeyValue> orderedKeyValueIterator() {
        return new Iterator<KeyValue> () {

            final Iterator<String> keyIterator = keyValueMap.navigableKeySet().iterator();

            @Override
            public boolean hasNext() {
                return keyIterator.hasNext();
            }

            @Override
            public KeyValue next() {
                String key = keyIterator.next();
                return new KeyValue(key, keyValueMap.get(key));
            }
        };
    }

    @Override
    public void destroy() {
        try {
            file.close();
            rawFile.delete();
        } catch (Exception e) {
            throw new SegmentDestroyFailException(e);
        }
    }

    @Override
    public long getId() {
        return this.id;
    }

}
