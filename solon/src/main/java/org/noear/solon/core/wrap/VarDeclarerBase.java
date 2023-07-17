package org.noear.solon.core.wrap;

import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.Constants;

import java.lang.reflect.AnnotatedElement;

/**
 * 变量申明者 基类
 *
 * @author noear
 * @since 2.4
 */
public abstract class VarDeclarerBase implements VarDeclarer {
    private AnnotatedElement element;
    private String name;
    private String defaultValue;

    private boolean isRequiredInput;

    private boolean isRequiredBody;
    private boolean isRequiredHeader;
    private boolean isRequiredCookie;
    private boolean isRequiredPath;



    @Override
    public boolean isRequiredBody() {
        return isRequiredBody;
    }

    @Override
    public boolean isRequiredHeader() {
        return isRequiredHeader;
    }

    @Override
    public boolean isRequiredCookie() {
        return isRequiredCookie;
    }

    @Override
    public boolean isRequiredPath() {
        return isRequiredPath;
    }

    @Override
    public boolean isRequiredInput() {
        return isRequiredInput;
    }

    @Override
    public String getRequiredHint() {
        if (isRequiredHeader) {
            return "Required header @" + getName();
        } else if (isRequiredCookie) {
            return "Required cookie @" + getName();
        } else {
            return "Required parameter @" + getName();
        }
    }

    @Override
    public String getName() {
        return name;
    }



    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    public VarDeclarerBase(AnnotatedElement element, String name){
        this.element = element;
        this.name = name;
    }

    protected void init(){
        if (resolveBody() == false) {
            if (resolveParam() == false) {
                if (resolvePathVar() == false) {
                    if (resolvePath() == false) {
                        if (resolveHeader() == false) {
                            resolveCookie();
                        }
                    }
                }
            }
        }
    }

    /////////////////

    /**
     * 分析 body 注解
     */
    private boolean resolveBody() {
        Body bodyAnno = element.getAnnotation(Body.class);

        if (bodyAnno == null) {
            return false;
        }

        isRequiredBody = true;
        return true;
    }

    /**
     * 分析 param 注解
     */
    private boolean resolveParam() {
        Param paramAnno = element.getAnnotation(Param.class);

        if (paramAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
        if (Utils.isNotEmpty(name2)) {
            name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(paramAnno.defaultValue()) == false) {
            defaultValue = paramAnno.defaultValue();
        }

        isRequiredInput = paramAnno.required();

        return true;
    }

    @Deprecated
    private boolean resolvePathVar() {
        PathVar paramAnno = element.getAnnotation(PathVar.class);

        if (paramAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
        if (Utils.isNotEmpty(name2)) {
            name = name2;
        }

        isRequiredPath = true;
        isRequiredInput = true;
        return true;
    }

    private boolean resolvePath() {
        Path paramAnno = element.getAnnotation(Path.class);

        if (paramAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
        if (Utils.isNotEmpty(name2)) {
            name = name2;
        }

        isRequiredPath = true;
        isRequiredInput = true;
        return true;
    }

    /**
     * 分析 header 注解
     */
    private boolean resolveHeader() {
        Header headerAnno = element.getAnnotation(Header.class);

        if (headerAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(headerAnno.value(), headerAnno.name());
        if (Utils.isNotEmpty(name2)) {
            name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(headerAnno.defaultValue()) == false) {
            defaultValue = headerAnno.defaultValue();
        }

        isRequiredInput = headerAnno.required();
        isRequiredHeader = true;

        return true;
    }

    /**
     * 分析 cookie 注解
     */
    private boolean resolveCookie() {
        Cookie cookieAnno = element.getAnnotation(Cookie.class);

        if (cookieAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(cookieAnno.value(), cookieAnno.name());
        if (Utils.isNotEmpty(name2)) {
            name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(cookieAnno.defaultValue()) == false) {
            defaultValue = cookieAnno.defaultValue();
        }

        isRequiredInput = cookieAnno.required();
        isRequiredCookie = true;

        return true;
    }
}
