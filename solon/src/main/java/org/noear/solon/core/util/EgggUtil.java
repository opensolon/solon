package org.noear.solon.core.util;

import org.noear.eggg.*;
import org.noear.solon.core.wrap.FieldEgggSpec;
import org.noear.solon.core.wrap.ParamEgggSpec;
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

    private static String doAliasHandle(ClassEggg cw, Object h, Object digest, String ref) {
        if (digest instanceof VarSpec) {
            return ((VarSpec) digest).getName();
        }

        return ref;
    }

    private static VarSpec doDigestHandle(ClassEggg cw, Object h, AnnotatedElement e, VarSpec ref) {
        if (h instanceof FieldEggg) {
            return new FieldEgggSpec((FieldEggg) h);
        } else if (h instanceof ParamEggg) {
            return new ParamEgggSpec((ParamEggg) h);
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