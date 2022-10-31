package feign.solon;

import feign.Feign;

@FunctionalInterface
public interface FeignConfiguration {
    Feign.Builder config(FeignClient client, Feign.Builder builder);
}
