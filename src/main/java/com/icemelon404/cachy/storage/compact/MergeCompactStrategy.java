package com.icemelon404.cachy.storage.compact;

import com.icemelon404.cachy.storage.KeyValue;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;

import java.util.*;

public class MergeCompactStrategy implements CompactStrategy {

    @Override
    public OrderedReactiveReadableSegment compact(Collection<OrderedReactiveReadableSegment> compactTargets, CompactWriter compactDest) {
        Iterator<KeyValue>[] keyValueSource = getIterators(compactTargets);
        KeyValue[] values = new KeyValue[compactTargets.size()];
        while (!isEmpty(values) || !isIteratorsFinished(keyValueSource)) {
            refillValues(values, keyValueSource);
            KeyValue minKeyValuePair = getMinKeyValuePair(values);
            assert minKeyValuePair != null;
            if (!isKeyDeleted(minKeyValuePair))
                compactDest.write(minKeyValuePair);
            removeDuplicate(values, minKeyValuePair.key);
        }
        return compactDest.finish();
    }

    private boolean isKeyDeleted(KeyValue keyValue) {
        return keyValue.value == null;
    }

    private void refillValues(KeyValue[] values, Iterator<KeyValue>[] keyValueSource) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null)
                values[i] = nextKeyValueOrNull(keyValueSource[i]);
        }
    }

    private Iterator<KeyValue>[] getIterators(Collection<OrderedReactiveReadableSegment> compactTargets) {
        List<OrderedReactiveReadableSegment> list = new ArrayList<>(compactTargets);
        Collections.sort(list);
        Iterator<KeyValue>[] keyValueSource = new Iterator[compactTargets.size()];
        int idx = 0;
        for (OrderedReactiveReadableSegment segment : list) {
            keyValueSource[idx] = segment.orderedKeyValueIterator();
            idx++;
        }
        return keyValueSource;
    }

    private KeyValue nextKeyValueOrNull(Iterator<KeyValue>  source) {
        if (source.hasNext())
            return source.next();
        return null;
    }

    private boolean isIteratorsFinished(Iterator<?>[] iterators) {
        for (Iterator<?> iterator : iterators) {
            if (iterator.hasNext())
                return false;
        }
        return true;
    }

    private boolean isEmpty(KeyValue[] values) {
        for (KeyValue value : values) {
            if (value != null)
                return false;
        }
        return true;
    }

    private KeyValue getMinKeyValuePair(KeyValue[] keyValuePairs) {

        KeyValue priorMin = null;
        for (KeyValue keyValue : keyValuePairs) {
            if (keyValue == null)
                continue;
            if (priorMin == null || keyValue.key.compareTo(priorMin.key) < 0)
                priorMin = keyValue;
        }
        return priorMin;

    }

    private void removeDuplicate(KeyValue[] keyValues, String keyToRemove) {
        for (int i = 0; i < keyValues.length; i++) {
            if (keyValues[i] == null)
                continue;
            if (keyValues[i].key.equals(keyToRemove))
                keyValues[i] = null;
        }
    }


}
