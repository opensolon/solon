/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */
package tech.smartboot.feat.core.common.codec.h2.hpack;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/*
 * Adds reverse lookup to SimpleHeaderTable. Separated from SimpleHeaderTable
 * for performance reasons. Decoder does not need this functionality. On the
 * other hand, Encoder does.
 */
/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
final class HeaderTable extends SimpleHeaderTable {

    /* An immutable map of static header fields' indexes */
    private static final Map<String, Map<String, Integer>> staticIndexes;

    static {
        Map<String, Map<String, Integer>> map
                = new HashMap<>(STATIC_TABLE_LENGTH);
        for (int i = 1; i <= STATIC_TABLE_LENGTH; i++) {
            HeaderField f = staticTable.get(i);
            Map<String, Integer> values
                    = map.computeIfAbsent(f.name, k -> new HashMap<>());
            values.put(f.value, i);
        }
        // create an immutable deep copy
        Map<String, Map<String, Integer>> copy = new HashMap<>(map.size());
        for (Map.Entry<String, Map<String, Integer>> e : map.entrySet()) {
            copy.put(e.getKey(), new HashMap<>(e.getValue()));
        }
        staticIndexes = new HashMap<>(copy);
    }

    //                name  ->    (value ->    [index])
    private final Map<String, Map<String, Deque<Long>>> map;
    private long counter = 1;

    public HeaderTable(int maxSize) {
        super(maxSize);
        map = new HashMap<>();
    }

    //
    // This method returns:
    //
    // * a positive integer i where i (i = [1..Integer.MAX_VALUE]) is an
    // index of an entry with a header (n, v), where n.equals(name) &&
    // v.equals(value)
    //
    // * a negative integer j where j (j = [-Integer.MAX_VALUE..-1]) is an
    // index of an entry with a header (n, v), where n.equals(name)
    //
    // * 0 if there's no entry e such that e.getName().equals(name)
    //
    // The rationale behind this design is to allow to pack more useful data
    // into a single invocation, facilitating a single pass where possible
    // (the idea is the same as in java.util.Arrays.binarySearch(int[], int)).
    //
    public int indexOf(CharSequence name, CharSequence value) {
        // Invoking toString() will possibly allocate Strings for the sake of
        // the search, which doesn't feel right.
        String n = name.toString();
        String v = value.toString();

        // 1. Try exact match in the static region
        Map<String, Integer> values = staticIndexes.get(n);
        if (values != null) {
            Integer idx = values.get(v);
            if (idx != null) {
                return idx;
            }
        }
        // 2. Try exact match in the dynamic region
        int didx = search(n, v);
        if (didx > 0) {
            return STATIC_TABLE_LENGTH + didx;
        } else if (didx < 0) {
            if (values != null) {
                // 3. Return name match from the static region
                return -values.values().iterator().next(); // Iterator allocation
            } else {
                // 4. Return name match from the dynamic region
                return -STATIC_TABLE_LENGTH + didx;
            }
        } else {
            if (values != null) {
                // 3. Return name match from the static region
                return -values.values().iterator().next(); // Iterator allocation
            } else {
                return 0;
            }
        }
    }

    @Override
    protected void add(HeaderField f) {
        super.add(f);
        Map<String, Deque<Long>> values = map.computeIfAbsent(f.name, k -> new HashMap<>());
        Deque<Long> indexes = values.computeIfAbsent(f.value, k -> new LinkedList<>());
        long counterSnapshot = counter++;
        indexes.add(counterSnapshot);
        assert indexesUniqueAndOrdered(indexes);
    }

    private boolean indexesUniqueAndOrdered(Deque<Long> indexes) {
        long maxIndexSoFar = -1;
        for (long l : indexes) {
            if (l <= maxIndexSoFar) {
                return false;
            } else {
                maxIndexSoFar = l;
            }
        }
        return true;
    }

    int search(String name, String value) {
        Map<String, Deque<Long>> values = map.get(name);
        if (values == null) {
            return 0;
        }
        Deque<Long> indexes = values.get(value);
        if (indexes != null) {
            return (int) (counter - indexes.peekLast());
        } else {
            assert !values.isEmpty();
            Long any = values.values().iterator().next().peekLast(); // Iterator allocation
            return -(int) (counter - any);
        }
    }

    @Override
    protected HeaderField remove() {
        HeaderField f = super.remove();
        Map<String, Deque<Long>> values = map.get(f.name);
        Deque<Long> indexes = values.get(f.value);
        Long index = indexes.pollFirst();
        if (indexes.isEmpty()) {
            values.remove(f.value);
        }
        assert index != null;
        if (values.isEmpty()) {
            map.remove(f.name);
        }
        return f;
    }
}
