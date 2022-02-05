package com.icemelon404.cachy.storage.reactive;

import com.icemelon404.cachy.storage.Destroyable;
import com.icemelon404.cachy.storage.Identifiable;
import com.icemelon404.cachy.storage.OrderedReadable;

public interface OrderedReactiveReadableSegment extends ReactiveReader, Identifiable, OrderedReadable, Destroyable {
}
