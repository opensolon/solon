package webapp.demo2_mvc;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XParam;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XSingleton;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;
import webapp.models.UserModel;

import java.util.Date;

@XSingleton(false)
@XMapping("/demo2/param")
@XController
public class ParamController {
    //支持post和get参数
    @XMapping("d/*")
    public String test_d(XContext context, String name) {
        return context.path();
    }


    //支持path var
    @XMapping("e/{p_q}/{obj}/{id}")
    public String test_e(XContext context, String p_q, String obj, String id) {
        return context.path() + "::" + p_q + "-" + obj + "-" + id;
    }

    //支持字符串数组参数（暂时只支持字符串数据）
    @XMapping("f")
    public String[] test_f(XContext context, String[] names) throws Exception{
        return names;
    }

    //支持上传文件参数
    @XMapping("g")
    public String test_g(XContext context, XFile file) throws Exception{
        return context.path();
    }
    
    //支持模型参数（要加@XParam申明，如安其字段里有日期类型，要加XParam指定格式）
    @XMapping("h1")
    public String test_h1(XContext context, @XParam UserModel model) throws Exception{
        return context.path();
    }

    //支持时间参数（要加XParam指定格式）
    @XMapping("h2")
    public String test_h2(XContext context, @XParam("yyyy-MM-dd") Date date) throws Exception{
        return context.path();
    }
}
