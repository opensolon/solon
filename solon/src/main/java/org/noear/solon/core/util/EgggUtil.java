package org.noear.solon.core.util;

import org.noear.eggg.ClassWrap;
import org.noear.eggg.Eggg;
import org.noear.eggg.TypeWrap;
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

    private static String doAliasHandle(ClassWrap cw, Object h, Object digest, String ref) {
        return ref;
    }

    private static VarSpec doDigestHandle(ClassWrap cw, Object h, AnnotatedElement e, VarSpec ref) {
        return ref;
    }

    public static TypeWrap getTypeWrap(Type type) {
        return eggg.getTypeWrap(type);
    }
}