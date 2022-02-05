package com.icemelon404.cachy.storage.compact;

import com.icemelon404.cachy.storage.excpetion.CompactionFailException;
import com.icemelon404.cachy.storage.reactive.OrderedReactiveReadableSegment;
import com.icemelon404.cachy.storage.reactivecomposite.CompactResult;
import com.icemelon404.cachy.storage.common.Loader;
import com.icemelon404.cachy.storage.reactivecomposite.SegmentCompactor;

import java.util.*;

public class SegmentCompactorImpl implements SegmentCompactor {

    private final CompactTargetStrategy targetStrategy;
    private final CompactStrategy strategy;
    private final Loader<Long, ? extends CompactWriter> writerLoader;

    public SegmentCompactorImpl(CompactTargetStrategy targetStrategy,
                                CompactStrategy strategy,
                                Loader<Long, ? extends CompactWriter> writerLoader) {
        this.targetStrategy = targetStrategy;
        this.strategy = strategy;
        this.writerLoader = writerLoader;
    }

    @Override
    public CompactResult compact(TreeSet<OrderedReactiveReadableSegment> targets) {
        long newSegmentId = getMaxId(targets);
        try {
            Collection<OrderedReactiveReadableSegment> compactTargets = targetStrategy.getCompactTargets(targets);
            if (compactTargets.size() < 2)
                throw new CompactionFailException("컴팩트 가능한 세그먼트 갯수 미달");
            CompactWriter writeTarget = writerLoader.load(newSegmentId);
            return new CompactResult(compactTargets, strategy.compact(compactTargets, writeTarget));
        } catch (Exception e) {
            throw new CompactionFailException("컴팩션에 실패하였습니다");
        }
    }


    private long getMaxId(Collection<OrderedReactiveReadableSegment> targets) {
        Optional<OrderedReactiveReadableSegment> optionalMax = targets.stream().min(Comparator.naturalOrder());
        if (!optionalMax.isPresent())
            throw new CompactionFailException("컴팩션 후 세그먼트의 아이디를 결정하는 데 실패하였습니다");
        return optionalMax.get().getId();
    }

}
