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
package org.noear.solon.core.handle;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * mvc:模型与视图
 *
 * <pre>{@code
 * @Mapping("/")
 * @Controller
 * public class DemoController{
 *     @Mapping("login")
 *     public ModelAndView login(){
 *         ModelAndView mv = new ModelAndView("login.ftl");
 *         mv.put("slogan","欢迎登录");
 *
 *         return mv;
 *     }
 *
 *     @Mapping("")
 *     public ModelAndView home(){
 *         return new ModelAndView("home.ftl");
 *     }
 * }
 * }</pre>
 *
 * @author noear
 * @since 1.0
 * @since 1.9
 * */
public class ModelAndView implements Serializable {
    private String view;
    private Map<String, Object> model = new LinkedHashMap<>();

    public ModelAndView() {
        super();
    }

    public ModelAndView(String view) {
        this();
        this.view = view;
    }

    public ModelAndView(String view, Map<String, ?> model) {
        this(view);

        if (model != null) {
            this.model.putAll(model);
        }
    }


    /**
     * 获取视图
     */
    public String view() {
        return view;
    }

    /**
     * 设置视图
     */
    public ModelAndView view(String view) {
        this.view = view;
        return this;
    }

    /**
     * 获取模型
     */
    public Map<String, Object> model() {
        return model;
    }

    /**
     * 设置模型
     *
     * @since 2.2
     */
    public ModelAndView model(Map<String, Object> model) {
        this.model = model;
        return this;
    }

    /**
     * 添加模型值
     */
    public ModelAndView put(String key, Object value) {
        model.put(key, value);
        return this;
    }

    /**
     * 添加模型值
     */
    public ModelAndView putIfAbsent(String key, Object value) {
        model.putIfAbsent(key, value);
        return this;
    }

    /**
     * 添加模型值
     */
    public ModelAndView putAll(Map<String, ?> keyValues) {
        model.putAll(keyValues);
        return this;
    }

    /**
     * 清空
     */
    public void clear() {
        model.clear();
        view = null;
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() {
        return view == null && model.size() == 0;
    }
}
