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
 * */
public class ModelAndView implements Serializable {
    private String view;
    private Map<String, Object> model = new LinkedHashMap<>();

    public ModelAndView(){super();}
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


    /** 视图 */
    public String view() {
        return view;
    }
    public ModelAndView view(String view){
        this.view = view;
        return this;
    }

    /** 模型 */
    public Map<String, Object> model() { return model; }

    public void put(String key, Object value){
        model.put(key, value);
    }

    public void putIfAbsent(String key, Object value){
        model.putIfAbsent(key, value);
    }

    public void putAll(Map<String, ?> values){
        model.putAll(values);
    }

    public void clear() {
        model.clear();
        view = null;
    }

    /** 是否为空 */
    public boolean isEmpty() {
        return view == null && model.size()==0;
    }
}
