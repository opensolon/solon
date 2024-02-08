package org.noear.solon.core.mvc;

import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.Constants;
import org.noear.solon.core.handle.ActionParam;

import java.lang.reflect.AnnotatedElement;

/**
 * Action 参数分析器
 *
 * @author noear
 * @since 2.7
 */
public class ActionParamResolver {
    /**
     * 分析
     * */
    public static void resolve(ActionParam vo, AnnotatedElement element) {
        if (resolveBody(vo, element) == false) {
            if (resolveParam(vo, element) == false) {
                if (resolvePathVar(vo, element) == false) {
                    if (resolvePath(vo, element) == false) {
                        if (resolveHeader(vo, element) == false) {
                            resolveCookie(vo, element);
                        }
                    }
                }
            }
        }
    }

    /**
     * 分析 body 注解
     */
    private static boolean resolveBody(ActionParam vo, AnnotatedElement element) {
        Body bodyAnno = element.getAnnotation(Body.class);

        if (bodyAnno == null) {
            return false;
        }

        vo.isRequiredBody = true;
        return true;
    }

    /**
     * 分析 param 注解
     */
    private static boolean resolveParam(ActionParam vo, AnnotatedElement element) {
        Param paramAnno = element.getAnnotation(Param.class);

        if (paramAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
        if (Utils.isNotEmpty(name2)) {
            vo.name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(paramAnno.defaultValue()) == false) {
            vo.defaultValue = paramAnno.defaultValue();
        }

        vo.isRequiredInput = paramAnno.required();

        return true;
    }

    @Deprecated
    private static boolean resolvePathVar(ActionParam vo, AnnotatedElement element) {
        PathVar paramAnno = element.getAnnotation(PathVar.class);

        if (paramAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
        if (Utils.isNotEmpty(name2)) {
            vo.name = name2;
        }

        vo.isRequiredPath = true;
        vo.isRequiredInput = true;
        return true;
    }

    private static boolean resolvePath(ActionParam vo, AnnotatedElement element) {
        Path paramAnno = element.getAnnotation(Path.class);

        if (paramAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
        if (Utils.isNotEmpty(name2)) {
            vo.name = name2;
        }

        vo.isRequiredPath = true;
        vo.isRequiredInput = true;
        return true;
    }

    /**
     * 分析 header 注解
     */
    private static boolean resolveHeader(ActionParam vo, AnnotatedElement element) {
        Header headerAnno = element.getAnnotation(Header.class);

        if (headerAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(headerAnno.value(), headerAnno.name());
        if (Utils.isNotEmpty(name2)) {
            vo.name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(headerAnno.defaultValue()) == false) {
            vo.defaultValue = headerAnno.defaultValue();
        }

        vo.isRequiredInput = headerAnno.required();
        vo.isRequiredHeader = true;

        return true;
    }

    /**
     * 分析 cookie 注解
     */
    private static boolean resolveCookie(ActionParam vo, AnnotatedElement element) {
        Cookie cookieAnno = element.getAnnotation(Cookie.class);

        if (cookieAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(cookieAnno.value(), cookieAnno.name());
        if (Utils.isNotEmpty(name2)) {
            vo.name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(cookieAnno.defaultValue()) == false) {
            vo.defaultValue = cookieAnno.defaultValue();
        }

        vo.isRequiredInput = cookieAnno.required();
        vo.isRequiredCookie = true;

        return true;
    }
}
