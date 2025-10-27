package org.noear.solon.core.util;

import org.noear.eggg.*;
import org.noear.solon.core.wrap.FieldSpec;
import org.noear.solon.core.wrap.ParamSpec;
import org.noear.solon.core.wrap.VarSpec;

import java.lang.reflect.*;

/**
 *
 * @author noear
 * @since 3.7
 */
public class EgggUtil {
    private static final Eggg eggg = new Eggg()
            .withAliasHandler(EgggUtil::doAliasHandle)
            .withDigestHandler(EgggUtil::doDigestHandle)
            .withReflectHandler(new EgggReflectHandler());

    private static String doAliasHandle(ClassEggg cw, AnnotatedEggg s, String ref) {
        if (s.getDigest() instanceof VarSpec) {
            return s.<VarSpec>getDigest().getName();
        }

        return ref;
    }

    private static Object doDigestHandle(ClassEggg cw, AnnotatedEggg s, Object ref) {
        if (s instanceof FieldEggg) {
            return new FieldSpec((FieldEggg) s);
        } else if (s instanceof ParamEggg) {
            return new ParamSpec((ParamEggg) s);
        }

        return ref;
    }

    public static TypeEggg getTypeEggg(Type type) {
        return eggg.getTypeEggg(type);
    }

    public static ClassEggg getClassEggg(Type type) {
        return getTypeEggg(type).getClassEggg();
    }
}