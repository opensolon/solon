package client.dso;


import org.noear.nami.Nami;
import org.noear.nami.NamiConfiguration;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.encoder.SnackEncoder;

import java.util.function.Supplier;

public class FairyConfigurationImp implements NamiConfiguration {
    private Supplier<String> test = () -> "http://localhost:8080";

    @Override
    public void config(NamiClient client, Nami.Builder builder) {
        builder.encoder(SnackEncoder.instance);
        builder.upstream(test);
    }
}
