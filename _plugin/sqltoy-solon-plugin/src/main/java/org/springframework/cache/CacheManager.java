package org.springframework.cache;

import org.springframework.lang.Nullable;

import java.util.Collection;


/**
 * Spring's central cache manager SPI.
 *
 * <p>Allows for retrieving named {@link Cache} regions.
 *
 * @author Costin Leau
 * @author Sam Brannen
 * @since 3.1
 */
public interface CacheManager {

    /**
     * Get the cache associated with the given name.
     * <p>Note that the cache may be lazily created at runtime if the
     * native provider supports it.
     * @param name the cache identifier (must not be {@code null})
     * @return the associated cache, or {@code null} if such a cache
     * does not exist or could be not created
     */
    @Nullable
    Cache getCache(String name);

    /**
     * Get a collection of the cache names known by this manager.
     * @return the names of all caches known by the cache manager
     */
    Collection<String> getCacheNames();

}
