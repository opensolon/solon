/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
