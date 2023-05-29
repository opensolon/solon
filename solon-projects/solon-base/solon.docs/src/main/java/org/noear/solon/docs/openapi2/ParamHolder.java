package org.noear.solon.docs.openapi2;

import io.swagger.annotations.ApiImplicitParam;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Cookie;
import org.noear.solon.annotation.Header;
import org.noear.solon.annotation.Path;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.docs.ApiEnum;

import java.util.Collection;

/**
 * @author noear 2023/5/25 created
 */
public class ParamHolder {
    private ParamWrap param;
    private ApiImplicitParam anno;

    public ParamHolder(ParamWrap param){
        this.param = param;
    }

    public ParamHolder binding(ApiImplicitParam anno) {
        this.anno = anno;
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }

    public ParamWrap getParam() {
        return param;
    }

    public ApiImplicitParam getAnno() {
        return anno;
    }

    /**
     * 名字
     * */
    public String getName() {
        if(param != null){
            return param.getName();
        }

        if(anno != null){
            return anno.name();
        }

        return null;
    }

    /**
     * 描述
     * */
    public String getDescription(){
        if(anno != null){
            return anno.value();
        }

        return null;
    }

    /**
     * 获取数据类型
     * */
    public String dataType() {
        if (param != null) {
            if (UploadedFile.class.equals(param.getType())) {
                return ApiEnum.FILE;
            }

            return param.getType().getSimpleName();
        }

        String tmp = null;
        if (anno != null) {
            tmp = anno.dataType();
        }

        if (Utils.isBlank(tmp)) {
            return ApiEnum.STRING;
        } else {
            return tmp;
        }
    }

    public String paramType(){
        if(param != null) {
            if (param.requireBody()) {
                return ApiEnum.PARAM_TYPE_BODY;
            }
        }

        String tmp = null;
        if (anno != null) {
            tmp = anno.paramType();
        }

        if (Utils.isBlank(tmp)) {
            return ApiEnum.PARAM_TYPE_QUERY;
        } else {
            return tmp;
        }
    }

    public boolean allowMultiple() {
        if (param != null) {
            return param.getType().isArray() ||
                    Collection.class.isAssignableFrom(param.getType());
        }

        if (anno != null) {
            return anno.allowMultiple();
        }

        return false;
    }

    public boolean isRequired() {
        if (param != null) {
            if (param.required()) {
                return true;
            }
        }

        if (anno != null) {
            return anno.required();
        }

        return false;
    }

    public boolean isRequiredBody(){
        if (param != null) {
            if (param.requireBody()) {
                return true;
            }
        }

        return false;
    }

    public boolean isRequiredHeader(){
        if (param != null) {
            if (param.getParameter().isAnnotationPresent(Header.class)) {
                return true;
            }
        }

        return false;
    }

    public boolean isRequiredCookie(){
        if (param != null) {
            if (param.getParameter().isAnnotationPresent(Cookie.class)) {
                return true;
            }
        }

        return false;
    }

    public boolean isRequiredPath(){
        if (param != null) {
            if (param.getParameter().isAnnotationPresent(Path.class)) {
                return true;
            }
        }

        return false;
    }

    public boolean isReadOnly(){
        if(anno != null){
            return anno.readOnly();
        }

        return false;
    }

    public boolean isIgnore(){
        if(param !=null){
            if(Context.class.equals(param.getType())){
                return true;
            }

            if(SessionState.class.equals(param.getType())){
                return true;
            }
        }

        return false;
    }
}
