package com.icemelon404.cachy.storage;


public abstract class AbstractIdComparable implements Identifiable {

    @Override
    public int compareTo(Identifiable readableSegment) {
        if (readableSegment.getId() != getId())
            return Long.compare(readableSegment.getId(), getId());
        return Long.compare(this.hashCode(), readableSegment.hashCode());
    }
}
