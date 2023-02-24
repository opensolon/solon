package org.noear.solon.proxy.apt;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * @author noear 2023/2/24 created
 */
public class GenericExecutableElementImpl implements ExecutableElement {
    ExecutableElement real;
    public GenericExecutableElementImpl(ExecutableElement real) {
        this.real = real;
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return null;
    }

    @Override
    public TypeMirror getReturnType() {
        return null;
    }

    @Override
    public List<? extends VariableElement> getParameters() {
        return null;
    }

    @Override
    public TypeMirror getReceiverType() {
        return null;
    }

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        return null;
    }

    @Override
    public AnnotationValue getDefaultValue() {
        return null;
    }

    @Override
    public TypeMirror asType() {
        return null;
    }

    @Override
    public ElementKind getKind() {
        return null;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return null;
    }

    @Override
    public Name getSimpleName() {
        return null;
    }

    @Override
    public Element getEnclosingElement() {
        return null;
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return null;
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return null;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return null;
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return null;
    }
}
