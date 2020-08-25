package org.noear.solon.extend.feign;

import feign.Feign;

@FunctionalInterface
public interface FeignConfiguration {
    void config(FeignClient client, Feign.Builder builder);
}
