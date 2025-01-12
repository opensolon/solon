/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.docs.openapi3.impl;

import io.swagger.v3.oas.annotations.Parameter;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.docs.ApiEnum;

import java.util.Collection;
import java.util.Map;

/**
 * @author noear
 * @since 2.4
 */
public class ParamHolder {
    private ParamWrap param;
    private Parameter anno;

    public ParamHolder(ParamWrap param){
        this.param = param;
    }

    public ParamHolder binding(Parameter anno) {
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

    public Parameter getAnno() {
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
            return anno.description();
        }

        return null;
    }

    public boolean isMap(){
        if(param != null){
            return Map.class.isAssignableFrom(param.getType());
        }

        return false;
    }

    public boolean isArray(){
        if(param != null){
            return Collection.class.isAssignableFrom(param.getType());
        }

        return false;
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
//        if (anno != null) {
//            tmp = anno.dataType();
//        }

        if (Utils.isBlank(tmp)) {
            return ApiEnum.STRING;
        } else {
            return tmp;
        }
    }

    public String paramType(){
        if(param != null) {
            if (param.isRequiredBody()) {
                return ApiEnum.PARAM_TYPE_BODY;
            }
        }

        String tmp = null;
        if (anno != null) {
            tmp = anno.in().toString();
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

//        if (anno != null) {
//            return anno.allowMultiple();
//        }

        return false;
    }

    public boolean isRequired() {
        if (param != null) {
            if (param.isRequiredInput()) {
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
            return param.isRequiredBody();
        }

        return false;
    }

    public boolean isRequiredHeader(){
        if (param != null) {
            return param.isRequiredHeader();
        }

        return false;
    }

    public boolean isRequiredCookie(){
        if (param != null) {
            return param.isRequiredCookie();
        }

        return false;
    }

    public boolean isRequiredPath(){
        if (param != null) {
            return param.isRequiredPath();
        }

        return false;
    }

    public boolean isReadOnly(){
//        if(anno != null){
//            return anno.readOnly();
//        }

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
