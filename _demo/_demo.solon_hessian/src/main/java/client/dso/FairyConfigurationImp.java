package client.dso;

import org.noear.fairy.Fairy;
import org.noear.fairy.FairyConfiguration;
import org.noear.fairy.Upstream;
import org.noear.fairy.annotation.FairyClient;
import org.noear.fairy.encoder.SnackTypeEncoder;

public class FairyConfigurationImp implements FairyConfiguration {
    private Upstream test = () -> "http://localhost:8080";

    @Override
    public void config(FairyClient client, Fairy.Builder builder) {

        builder.encoder(SnackTypeEncoder.instance);
        builder.upstream(test);
    }
}
