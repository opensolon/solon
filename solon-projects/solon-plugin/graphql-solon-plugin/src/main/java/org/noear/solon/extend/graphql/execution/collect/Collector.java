package org.noear.solon.extend.graphql.execution.collect;

import java.util.List;

/**
 * @author fuzi1996
 * @since 2.3
 */
public interface Collector<T> {

    void append(T one);

    List<T> getAllCollector();
}
