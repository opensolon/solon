package webapp.dso;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.noear.solon.extend.feign.FeignClient;
import org.noear.solon.extend.feign.FeignConfiguration;

public class JacksonConfig implements FeignConfiguration {
    @Override
    public Feign.Builder config(FeignClient client, Feign.Builder builder) {
        return builder.encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder());
    }
}
