package org.noear.solon.extend.feign;

import feign.Feign;

public class FeignConfigurationDefault implements FeignConfiguration{

    @Override
    public void config(FeignClient client, Feign.Builder builder) {

    }
}
