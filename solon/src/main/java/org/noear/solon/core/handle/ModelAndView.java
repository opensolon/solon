package org.noear.solon.core.handle;

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
public class ModelAndView extends LinkedHashMap{
    private transient String view;

    public ModelAndView(){super();}
    public ModelAndView(String view) {
        this();
        this.view = view;
    }
    public ModelAndView(String view, Map<String, ?> model) {
        this(view);

        if (model != null) {
            this.putAll(model);
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
    public Map<String, Object> model() { return this; }

    @Override
    public void clear() {
        super.clear();
        view = null;
    }

    /** 是否为空 */
    public boolean isEmpty() {
        return view == null && size()==0;
    }
}
