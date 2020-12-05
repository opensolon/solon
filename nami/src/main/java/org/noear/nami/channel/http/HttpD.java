package org.noear.nami.channel.http;

import org.noear.nami.Decoder;
import org.noear.nami.Encoder;
import org.noear.nami.Nami;
import org.noear.nami.decoder.SnackDecoder;
import org.noear.nami.encoder.SnackTypeEncoder;

import java.net.URI;

public class HttpD {
    public static <T> T create(String serverUri, Class<T> service) {
        return create(URI.create(serverUri), service);
    }

    public static <T> T create(URI serverUri, Class<T> service) {
        return create(serverUri, SnackTypeEncoder.instance, SnackDecoder.instance, service);
    }

    public static <T> T create(URI serverUri, Encoder encoder, Decoder decoder, Class<T> service) {
        String server = serverUri.getScheme() + ":" + serverUri.getAuthority();

        return Nami.builder()
                .encoder(encoder)
                .decoder(decoder)
                .channel(new HttpChannel())
                .upstream(() -> server)
                .create(service);
    }
}
