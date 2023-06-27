package org.noear.nami.annotation;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 2.3
 */
public class NamiBodyAnno implements NamiBody{
    private Body anno;
    public NamiBodyAnno(Body anno){
        this.anno = anno;
    }

    @Override
    public String contentType() {
        return anno.contentType();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return NamiBody.class;
    }
}
