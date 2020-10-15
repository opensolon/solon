package org.noear.solon.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * mvc:模型与视图
 *
 * @author noear
 * @since 1.0
 * */
public class ModelAndView extends LinkedHashMap{
    private transient String __view;

    public ModelAndView(){super();}
    public ModelAndView(String view) {
        this();
        __view = view;
    }
    public ModelAndView(String view, Map<String, ?> model) {
        this(view);

        if (model != null) {
            this.putAll(model);
        }
    }

    /** 视图 */
    public String view() {
        return __view;
    }
    public ModelAndView view(String view){
        __view = view;
        return this;
    }

    /** 模型 */
    public Map<String, Object> model() { return this; }

    @Override
    public void clear() {
        super.clear();
        __view = null;
    }

    /** 是否为空 */
    public boolean isEmpty() {
        return __view == null && size()==0;
    }
}
