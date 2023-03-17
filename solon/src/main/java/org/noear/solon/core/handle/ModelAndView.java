package org.noear.solon.core.handle;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * mvc:模型与视图
 *
 * <pre><code>
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
 * </code></pre>
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
