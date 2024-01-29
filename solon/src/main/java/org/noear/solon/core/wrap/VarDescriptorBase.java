package org.noear.solon.core.wrap;

import org.noear.solon.core.FactoryManager;
import org.noear.solon.core.handle.ActionParam;

import java.lang.reflect.AnnotatedElement;

/**
 * 变量描述符 基类
 *
 * @author noear
 * @since 2.4
 */
public abstract class VarDescriptorBase implements VarDescriptor {
    private final AnnotatedElement element;
    private final ActionParam vo = new ActionParam();

    @Override
    public boolean isRequiredBody() {
        return vo.isRequiredBody;
    }

    @Override
    public boolean isRequiredHeader() {
        return vo.isRequiredHeader;
    }

    @Override
    public boolean isRequiredCookie() {
        return vo.isRequiredCookie;
    }

    @Override
    public boolean isRequiredPath() {
        return vo.isRequiredPath;
    }

    @Override
    public boolean isRequiredInput() {
        return vo.isRequiredInput;
    }

    @Override
    public String getRequiredHint() {
        if (vo.isRequiredHeader) {
            return "Required header @" + getName();
        } else if (vo.isRequiredCookie) {
            return "Required cookie @" + getName();
        } else {
            return "Required parameter @" + getName();
        }
    }

    @Override
    public String getName() {
        return vo.name;
    }


    @Override
    public String getDefaultValue() {
        return vo.defaultValue;
    }

    public VarDescriptorBase(AnnotatedElement element, String name) {
        this.element = element;
        this.vo.name = name;
    }

    protected void init() {
        FactoryManager.mvcFactory().resolveActionParam(vo, element);
    }

    /////////////////
}
