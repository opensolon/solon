package client.dso;

import org.noear.fairy.Fairy;
import org.noear.fairy.FairyConfiguration;
import org.noear.fairy.annotation.FairyClient;
import org.noear.fairy.encoder.SnackTypeEncoder;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XUpstream;

public class FairyConfigurationImp implements FairyConfiguration {
    @Override
    public void config(FairyClient client, Fairy.Builder builder) {
        String sev = client.value().split(":")[0];
        XUpstream upstream = XBridge.upstreamFactory().create(sev);

        builder.encoder(SnackTypeEncoder.instance);
        builder.upstream(upstream::getServer);
    }
}
