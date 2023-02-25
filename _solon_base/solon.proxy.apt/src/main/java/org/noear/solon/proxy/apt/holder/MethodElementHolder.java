package org.noear.solon.proxy.apt.holder;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * （增加泛型支持）
 *
 * @author noear
 * @since 2.2
 */
public class MethodElementHolder implements ExecutableElement {
    ExecutableElement real;
    List<TypeParameterElement> typeParameters = new ArrayList<>();
    List<VariableElement> parameters = new ArrayList<>();
    TypeMirror returnType;
    public MethodElementHolder(ExecutableElement real, Map<String, TypeMirror> gtArgMap) {
        this.real = real;

        //泛型申明
        for(TypeParameterElement t1 : real.getTypeParameters()){
            if (t1.asType() instanceof TypeVariable) {
                TypeMirror t1Type = gtArgMap.get(t1.asType().toString());

                if(t1Type == null){
                    typeParameters.add(t1);
                }else{
                    typeParameters.add(new TypeParamElementHolder(t1, t1Type));
                }
            } else {
                typeParameters.add(t1);
            }
        }

        //参数
        for (VariableElement p1 : real.getParameters()) {
            TypeMirror p2 = p1.asType();
            if (p2 instanceof TypeVariable) {
                TypeMirror p1Type = gtArgMap.get(p2.toString());

                if (p1Type == null) {
                    p1Type = ((TypeVariable) real.getReturnType()).getUpperBound();
                }

                parameters.add(new ParamElementHolder(p1, p1Type));
            } else {
                if (p2 instanceof DeclaredType) {
                    DeclaredType p3 = (DeclaredType) (p2);
                    TypeMirror p4= new DeclaredTypeHolder(p3, gtArgMap);

                    parameters.add(new ParamElementHolder(p1, p4));
                } else {
                    //List<T>
                    //getTypeArguments
                    parameters.add(p1);
                }
            }
        }

        //返回值
        TypeMirror r0 = real.getReturnType();
        if(r0 instanceof TypeVariable){
            returnType = gtArgMap.get(r0.toString());

            if(returnType == null){
                returnType = r0;
            }
        }else{
            if(r0 instanceof DeclaredType){
                DeclaredType p3 = (DeclaredType) (r0);
                returnType = new DeclaredTypeHolder(p3, gtArgMap);
            }else {
                returnType = real.getReturnType();
            }
        }
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return typeParameters;
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
