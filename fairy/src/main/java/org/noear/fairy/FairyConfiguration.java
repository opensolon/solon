package org.noear.fairy;

import org.noear.fairy.annotation.FairyClient;

@FunctionalInterface
public interface FairyConfiguration {
    void config(FairyClient client, Fairy.Builder builder);
}
