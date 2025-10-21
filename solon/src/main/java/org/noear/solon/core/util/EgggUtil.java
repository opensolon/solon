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
            .<VarSpec>withAliasHandler(x -> x.getName())
            .<VarSpec>withDigestHandler(EgggUtil::doDigestHandle)
            .withReflectHandler(new EgggReflectHandler());

    private static VarSpec doDigestHandle(ClassWrap cw, Object h, AnnotatedElement e, VarSpec ref) {
        return null;
    }

    public static TypeWrap getTypeWrap(Type type) {
        return eggg.getTypeWrap(type);
    }
}
