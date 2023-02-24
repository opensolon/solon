package org.noear.solon.proxy.apt.impl;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author noear
 * @since 2.2
 */
public class MethodElementHolder implements ExecutableElement {
    ExecutableElement real;
    List<VariableElement> parameters;
    TypeMirror returnType;
    public MethodElementHolder(ExecutableElement real, Map<String, TypeMirror> gtArgMap) {
        this.real = real;
        this.parameters = new ArrayList<>();

        for (VariableElement p1 : real.getParameters()) {
            if (p1.asType() instanceof TypeVariable) {
                TypeMirror p1Type = gtArgMap.get(p1.asType().toString());
                parameters.add(new ParamElementHolder(p1, p1Type));
            } else {
                parameters.add(p1);
            }
        }

        if(real.getReturnType() instanceof TypeVariable){
            returnType = gtArgMap.get(real.getReturnType().toString());
        }else{
            returnType = real.getReturnType();
        }
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return real.getTypeParameters();
    }

    @Override
    public TypeMirror getReturnType() {
        return returnType;
    }

    @Override
    public List<? extends VariableElement> getParameters() {
        return parameters;
    }

    @Override
    public TypeMirror getReceiverType() {
        return real.getReceiverType();
    }

    @Override
    public boolean isVarArgs() {
        return real.isVarArgs();
    }

    @Override
    public boolean isDefault() {
        return real.isDefault();
    }

    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        return real.getThrownTypes();
    }

    @Override
    public AnnotationValue getDefaultValue() {
        return getDefaultValue();
    }

    @Override
    public TypeMirror asType() {
        return real.asType();
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
        return real.accept(v,p);
    }
}
