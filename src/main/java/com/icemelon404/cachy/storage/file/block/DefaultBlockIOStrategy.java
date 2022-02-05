package com.icemelon404.cachy.storage.file.block;

import com.icemelon404.cachy.storage.excpetion.SegmentCreateFailException;
import com.icemelon404.cachy.storage.excpetion.SegmentReadException;
import com.icemelon404.cachy.storage.KeyValue;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DefaultBlockIOStrategy implements BlockIOStrategy {

    @Override
    public void write(KeyValue keyVal, RandomAccessFile file) {
        try {
            writeTombStone(file, keyVal);
            writeKey(keyVal.key, file);
            if (keyVal.value != null)
                writeValue(keyVal.value, file);
        } catch (IOException exception) {
            throw new SegmentCreateFailException(exception, "");
        }
    }

    private void writeTombStone(RandomAccessFile file, KeyValue keyVal) throws IOException {
        if (keyVal.value == null)
            file.write((byte) 1);
        else
            file.write((byte) 0);
    }

    private void writeKey(String key, RandomAccessFile target) throws IOException {
        byte[] keyBytes = key.getBytes();
        target.writeInt(keyBytes.length);
        target.write(keyBytes);
    }

    private void writeValue(byte[] value, RandomAccessFile target) throws IOException {
        target.writeInt(value.length);
        target.write(value);
    }

    @Override
    public KeyValue read(RandomAccessFile file) {
        try {
            boolean isDeleted = readTombStone(file);
            String key = readKey(file);
            if (isDeleted)
                return new KeyValue(key, null);
            return new KeyValue(key, readValue(file));
        } catch (Exception e) {
            throw new SegmentReadException(e, "읽기 중 오류가 발생하였습니다");
        }
    }

    private boolean readTombStone(RandomAccessFile file) throws IOException {
        byte tombStone = file.readByte();
        return tombStone == (byte) 1;
    }

    private String readKey(RandomAccessFile file) throws IOException {
        int keySize = file.readInt();
        byte[] keyBytes = new byte[keySize];
        int readSize = file.read(keyBytes);
        if (keySize != readSize)
            throw new SegmentReadException("잘못된 읽기입니다");
        return new String(keyBytes);
    }

    private byte[] readValue(RandomAccessFile file) throws IOException {
        int valueSize = file.readInt();
        byte[] value = new byte[valueSize];
        int readSize = file.read(value);
        if (readSize != valueSize)
            throw new SegmentReadException("잘못된 읽기입니다");
        return value;
    }
}
