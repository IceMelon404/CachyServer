package com.icemelon404.cachy.storage.blocking;

import com.icemelon404.cachy.storage.OrderedReadable;
import com.icemelon404.cachy.storage.Destroyable;
import com.icemelon404.cachy.storage.Identifiable;

public interface OrderedBlockingReadableSegment extends BlockingReader, OrderedReadable, Identifiable, Destroyable {

}
