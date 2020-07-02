package webapp.demo2_mvc;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XParam;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XSingleton;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;
import org.noear.solon.core.XMethod;
import webapp.models.UserModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

@XSingleton(false)
@XMapping("/demo2/param")
@XController
public class ParamController {
    //支持post和get参数
    @XMapping("body")
    public String test_body(XContext ctx) throws IOException {
        return ctx.body();
    }

    @XMapping("decimal")
    public Object test_decimal(BigDecimal num) throws IOException {
        return num;
    }

    //支持post和get参数
    @XMapping("d/*")
    public String test_d(XContext ctx, String name) {
        return name;
    }


    //支持path var
    @XMapping("e/{p_q}/{obj}/{id}")
    public String test_e(XContext ctx, String p_q, String obj, String id) {
        return ctx.path() + "::" + p_q + "-" + obj + "-" + id;
    }

    //支持字符串数组参数（暂时只支持字符串数据）
    @XMapping("array_str")
    public String[] test_f(XContext ctx, String[] aaa, String ccc) throws Exception{
        return aaa;
    }

    @XMapping("array_Int")
    public Object test_f2(XContext ctx, Integer[] aaa, String ccc) throws Exception{
        return aaa;
    }

    @XMapping("array_int")
    public Object test_f3(XContext ctx, int[] aaa, String ccc) throws Exception{
        return aaa;
    }

    //支持上传文件参数
    @XMapping("file")
    public String test_g(XContext ctx,  String title, XFile file,  String label) throws Exception{
        return ctx.path();
    }
    
    //支持模型参数（要加@XParam申明，如安其字段里有日期类型，要加XParam指定格式）
    @XMapping("model")
    public void test_h1(XContext ctx, UserModel model) throws Throwable {
        ctx.render(model);
    }

    //支持时间参数（要加XParam指定格式）
    @XMapping("date")
    public Object test_h2(XContext ctx, @XParam("yyyy-MM-dd") Date date, Date date2) throws Exception{
        return date + " # " + date2;
    }

    @XMapping(value = "login", method = XMethod.POST)
    public Object test_h3(String username, String password) throws Exception{
        return username + " # " + password;
    }
}
