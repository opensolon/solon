package org.noear.solon.extend.graphql.execution.collect;

import java.util.LinkedList;
import java.util.List;

/**
 * @author fuzi1996
 * @since 2.3
 */
public abstract class AbstractBaseCollector<T> implements Collector<T> {

    protected List<T> graphqlResourceResolvers;

    public AbstractBaseCollector() {
        this.graphqlResourceResolvers = new LinkedList<>();
    }

    @Override
    public void append(T one) {
        this.graphqlResourceResolvers.add(one);
    }

    @Override
    public List<T> getAllCollector() {
        return graphqlResourceResolvers;
    }
}
