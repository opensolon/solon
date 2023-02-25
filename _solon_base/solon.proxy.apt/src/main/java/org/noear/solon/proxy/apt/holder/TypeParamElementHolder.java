package org.noear.solon.proxy.apt.holder;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * （增加泛型支持）
 *
 * @author noear
 * @since 2.2
 */
public class TypeParamElementHolder implements TypeParameterElement {
    TypeParameterElement real;
    TypeMirror type;

    public TypeParamElementHolder(TypeParameterElement real, TypeMirror type) {
        this.real = real;
        this.type = type;
    }

    @Override
    public Element getGenericElement() {
        return real.getGenericElement();
    }

    @Override
    public List<? extends TypeMirror> getBounds() {
        return real.getBounds();
    }

    @Override
    public TypeMirror asType() {
        return type;
    }

    @Override
    public ElementKind getKind() {
        return real.getKind();
    }

    @Override
    public Set<Modifier> getModifiers() {
        return real.getModifiers();
    }

    @Override
    public Name getSimpleName() {
        return real.getSimpleName();
    }

    @Override
    public Element getEnclosingElement() {
        return real.getEnclosingElement();
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return real.getEnclosedElements();
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return real.getAnnotationMirrors();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return real.getAnnotation(annotationType);
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return real.getAnnotationsByType(annotationType);
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return real.accept(v, p);
    }
}
