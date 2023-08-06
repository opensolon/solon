package feign.solon;

import feign.Feign;

public class FeignConfigurationDefault implements FeignConfiguration{

    @Override
    public Feign.Builder config(FeignClient client, Feign.Builder builder) {
        return builder;
    }
}
