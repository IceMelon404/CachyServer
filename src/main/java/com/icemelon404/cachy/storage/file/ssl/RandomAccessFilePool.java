package com.icemelon404.cachy.storage.file.ssl;

import com.icemelon404.cachy.storage.excpetion.SegmentDestroyFailException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RandomAccessFilePool  {

    private BlockingQueue<RandomAccessFile> pool = new LinkedBlockingQueue<>();
    private Set<RandomAccessFile> initial;

    public RandomAccessFilePool(File target, int size) throws FileNotFoundException {
        for (int i = 0; i < size; i++)
            pool.add(new RandomAccessFile(target, "r"));
        initial = Collections.unmodifiableSet(new HashSet<>(pool));
    }

    public RandomAccessFile get() throws InterruptedException {
        return pool.take();
    }

    public void retrieve(RandomAccessFile file) {
        pool.add(file);
    }

    public void close() {
        for (RandomAccessFile file : initial) {
            try {
                file.close();
            } catch (IOException e) {
                throw new SegmentDestroyFailException(e);
            }
        }
    }

}
