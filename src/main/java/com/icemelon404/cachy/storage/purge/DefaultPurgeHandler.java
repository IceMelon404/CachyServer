package com.icemelon404.cachy.storage.purge;

import com.icemelon404.cachy.storage.Destroyable;
import com.icemelon404.cachy.storage.reactivecomposite.PurgeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultPurgeHandler implements Runnable, PurgeHandler {

    private final ConcurrentHashMap<Destroyable, AtomicInteger> onReadCnt = new ConcurrentHashMap<>();
    private final BlockingQueue<Destroyable> purgeRequests = new LinkedBlockingQueue<>();
    private final Logger logger = LoggerFactory.getLogger(DefaultPurgeHandler.class);

    @Override
    public void markOnRead(Collection<? extends Destroyable> reading) {
        for (Destroyable d : reading)
            increaseOnReadCnt(d);
    }

    private void increaseOnReadCnt(Destroyable destroyable) {
        onReadCnt.putIfAbsent(destroyable, new AtomicInteger(0));
        onReadCnt.get(destroyable).incrementAndGet();
    }

    private void decreaseOnReadCnt(Destroyable destroyable) {
        if (onReadCnt.get(destroyable).decrementAndGet() < 0)
            throw new IllegalStateException();
    }

    private boolean canPurge(Destroyable destroyable) {
        return !onReadCnt.containsKey(destroyable) || onReadCnt.get(destroyable).get() == 0;
    }

    @Override
    public void markReadDone(Collection<? extends Destroyable> readings) {
        for (Destroyable destroyable : readings)
            decreaseOnReadCnt(destroyable);
    }

    @Override
    public void requestPurge(Collection<? extends Destroyable> destroyables) {
        purgeRequests.addAll(destroyables);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Destroyable purgeTarget = purgeRequests.take();
                if (!canPurge(purgeTarget)) {
                    purgeRequests.add(purgeTarget);
                    delay();
                    continue;
                }
                purgeTarget.destroy();
            } catch (InterruptedException ignored) { }
        }
    }

    private void delay() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ignored) { }
    }
}
