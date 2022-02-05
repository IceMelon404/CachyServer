package com.icemelon404.cachy.storage.reactivecomposite;

import com.icemelon404.cachy.storage.Destroyable;

import java.util.Collection;

public interface PurgeHandler {
    void markOnRead(Collection<? extends Destroyable> reading);
    void markReadDone(Collection<? extends Destroyable> readings);
    void requestPurge(Collection<? extends Destroyable> destroyables);
}
