package com.icemelon404.cachy.storage.propagate;

import com.icemelon404.cachy.storage.OrderedReadable;
import com.icemelon404.cachy.storage.blocking.BlockingReader;
import com.icemelon404.cachy.storage.blocking.BlockingWriter;
import com.icemelon404.cachy.storage.Identifiable;

public interface SizeAwareBlockingSegment extends Identifiable, OrderedReadable, BlockingReader, BlockingWriter {
    int getSize();
}
