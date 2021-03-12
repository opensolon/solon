package org.noear.solon.extend.feign;

import feign.Feign;

@FunctionalInterface
public interface FeignConfiguration {
    Feign.Builder config(FeignClient client, Feign.Builder builder);
}
