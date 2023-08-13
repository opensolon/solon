package graphql.solon.execution;

import java.util.List;
import java.util.Map;

/**
 * @author fuzi1996
 * @since 2.3
 */
public interface ThreadLocalAccessor {

    /**
     * Extract ThreadLocal values and add them to the given Map, so they can be
     * saved and subsequently {@link #restoreValues(Map) restored} around the
     * invocation of data fetchers and exception resolvers.
     *
     * @param container to add extracted ThreadLocal values to
     */
    void extractValues(Map<String, Object> container);

    /**
     * Restore ThreadLocal context by looking up previously
     * {@link #extractValues(Map) extracted} values.
     *
     * @param values previously extracted saved ThreadLocal values
     */
    void restoreValues(Map<String, Object> values);

    /**
     * Reset ThreadLocal context for the given, previously
     * {@link #extractValues(Map) extracted} and then
     * {@link #restoreValues(Map) restored} values.
     *
     * @param values previously extracted saved ThreadLocal values
     */
    void resetValues(Map<String, Object> values);

    /**
     * Create a composite accessor that applies all of the given ThreadLocal accessors.
     *
     * @param accessors the accessors to apply
     * @return the composite accessor
     */
    static ThreadLocalAccessor composite(List<ThreadLocalAccessor> accessors) {
        return new CompositeThreadLocalAccessor(accessors);
    }

}
