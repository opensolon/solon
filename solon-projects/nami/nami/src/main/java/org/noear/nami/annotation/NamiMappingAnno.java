package org.noear.nami.annotation;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 2.3
 */
public class NamiMappingAnno implements NamiMapping {
    private Mapping anno;

    public NamiMappingAnno(Mapping anno) {
        this.anno = anno;
    }

    @Override
    public String value() {
        return anno.value();
    }

    @Override
    public String[] headers() {
        return anno.headers();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return NamiMapping.class;
    }
}
