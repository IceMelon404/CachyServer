package com.icemelon404.cachy.storage.compact;

import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;

import java.util.*;

public class MostRecentCompactTargetStrategy implements CompactTargetStrategy {

    private final int maxLength;

    public MostRecentCompactTargetStrategy(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public Collection<OrderedReactiveReadableSegment> getCompactTargets(TreeSet<OrderedReactiveReadableSegment> segments) {

        Collection<OrderedReactiveReadableSegment> ret = new LinkedList<>();
        int cnt = 0;
        Iterator<OrderedReactiveReadableSegment> it = segments.iterator();
        while (cnt < maxLength && it.hasNext()) {
            ret.add(it.next());
            cnt++;
        }
        return ret;
    }

}
