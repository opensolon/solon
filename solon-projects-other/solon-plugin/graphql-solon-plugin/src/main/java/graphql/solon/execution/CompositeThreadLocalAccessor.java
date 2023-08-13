package graphql.solon.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author fuzi1996
 * @since 2.3
 */
class CompositeThreadLocalAccessor implements ThreadLocalAccessor {

    private final List<ThreadLocalAccessor> accessors;

    CompositeThreadLocalAccessor(List<ThreadLocalAccessor> accessors) {
        this.accessors = new ArrayList<>(accessors);
    }

    @Override
    public void extractValues(Map<String, Object> container) {
        this.accessors.forEach((accessor) -> accessor.extractValues(container));
    }

    @Override
    public void restoreValues(Map<String, Object> values) {
        this.accessors.forEach((accessor) -> accessor.restoreValues(values));
    }

    @Override
    public void resetValues(Map<String, Object> values) {
        this.accessors.forEach((accessor) -> accessor.resetValues(values));
    }

}
