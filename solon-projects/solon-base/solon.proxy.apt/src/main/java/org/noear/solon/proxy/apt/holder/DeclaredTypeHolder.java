package org.noear.solon.proxy.apt.holder;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * （增加泛型支持）
 *
 * @author noear
 * @since 2.2
 */
public class DeclaredTypeHolder implements DeclaredType {
    DeclaredType real;
    List<TypeMirror> typeArguments = new ArrayList<>();

    public DeclaredTypeHolder(DeclaredType real, Map<String, TypeMirror> gtArgMap) {
        this.real = real;
        
        for(TypeMirror t1 :real.getTypeArguments()){
            if (t1 instanceof TypeVariable) {
                TypeMirror t1Type = gtArgMap.get(t1.toString());

                if(t1Type == null){
                    typeArguments.add(t1);
                }else{
                    typeArguments.add(t1Type);
                }
            } else {
                typeArguments.add(t1);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(real.asElement().toString());
        if (typeArguments.size() > 0) {
            buf.append("<");
            for (TypeMirror t1 : typeArguments) {
                buf.append(t1.toString()).append(",");
            }
            buf.setLength(buf.length() - 1);
            buf.append(">");
        }
        return buf.toString();
    }

    @Override
    public Element asElement() {
        return real.asElement();
    }

    @Override
    public TypeMirror getEnclosingType() {
        return real.getEnclosingType();
    }

    @Override
    public List<? extends TypeMirror> getTypeArguments() {
        return typeArguments;
    }

    @Override
    public TypeKind getKind() {
        return real.getKind();
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return real.accept(v, p);
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
}
