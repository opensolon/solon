package org.noear.nami.integration.solon;

import org.noear.nami.NamiConfiguration;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.Solon;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 2.6
 */
public class NamiClientAnno implements NamiClient {
    private NamiClient anno;
    private String name;


    public NamiClientAnno(NamiClient anno) {
        this.anno = anno;
        this.name = Solon.cfg().getByTmpl(anno.name());
    }

    @Override
    public String url() {
        return anno.url();
    }

    @Override
    public String group() {
        return anno.group();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String path() {
        return anno.path();
    }

    @Override
    public String[] headers() {
        return anno.headers();
    }

    @Override
    public String[] upstream() {
        return anno.upstream();
    }

    @Override
    public int timeout() {
        return anno.timeout();
    }

    @Override
    public int heartbeat() {
        return anno.heartbeat();
    }

    @Override
    public boolean localFirst() {
        return anno.localFirst();
    }

    @Override
    public Class<? extends NamiConfiguration> configuration() {
        return anno.configuration();
    }

    @Override
    public Class<?> fallback() {
        return anno.fallback();
    }

    @Override
    public Class<?> fallbackFactory() {
        return anno.fallbackFactory();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return anno.annotationType();
    }
}
