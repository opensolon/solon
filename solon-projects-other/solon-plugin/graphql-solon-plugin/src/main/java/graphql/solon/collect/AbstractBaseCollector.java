package graphql.solon.collect;

import java.util.LinkedList;
import java.util.List;

/**
 * @author fuzi1996
 * @since 2.3
 */
public abstract class AbstractBaseCollector<T> implements Collector<T> {

    protected List<T> list;

    public AbstractBaseCollector() {
        this.list = new LinkedList<>();
    }

    @Override
    public void append(T one) {
        this.list.add(one);
    }

    @Override
    public List<T> getAllCollector() {
        return list;
    }
}
