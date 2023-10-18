package org.noear.solon.serialization.fury;

import io.fury.Fury;
import io.fury.ThreadSafeFury;
import io.fury.config.Language;

/**
 * @author noear
 * @since 2.5
 */
public class FuryUtil {
    public static final ThreadSafeFury fury = Fury.builder()
            .withAsyncCompilation(true)
            .withLanguage(Language.JAVA)
            .withRefTracking(true)
            .requireClassRegistration(false)
            .buildThreadSafeFury();
}
