package feature.controller;

import feature.controller.service.*;
import org.noear.snack.ONode;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.Result;
import org.noear.solon.extend.uapi.UapiCode;
import org.noear.solon.extend.uapi.UapiGateway;

@XController
@XMapping("/cmd/*")
public class CmdGateway extends UapiGateway {
    @Override
    protected void register() {
        Aop.beanOnloaded(()->{
            Aop.beanForeach((k,bw)->{
                if("api".equals(bw.tag())){
                    add(bw);
                }
            });
        });

        //开始计时
        addBefore(c -> c.attr("start", System.currentTimeMillis()));

        add(API_0.class);
        add(API_A_0_1.class);
        add(API_A_0_2.class);
        add(API_A_0_3.class);
        add(API_A_0_4.class);

        //控制渲染+签名
        addAfter(c -> {
            String json = ONode.loadObj(c.result).toJson();
            String json_md5 = md5(json + "#" + sign_salt);

            c.headerSet("Rock-Sign", json_md5);
            c.output(json);
            c.result = json;
        });

        //记录性能
        addAfter(c -> {
            long start = c.attr("start", 0);
            if (start > 0) {
                long times = System.currentTimeMillis() - start;
                System.out.println("using times: " + times);
            }
        });

        //写入日志
        addAfter(c -> {
            if (c.result != null) {
                System.out.println(c.result);
            }
        });
    }


    private String sign_salt = "1234";
    private String md5(String str) {
        return "xxx";
    }

    @Override
    public void renderDo(XContext c, Object obj) throws Throwable {
        if (obj instanceof UapiCode) {
            c.result = (Result.failure((UapiCode) obj));
        } else if (obj instanceof Throwable) {
            c.result = (Result.failure(new UapiCode((Throwable) obj)));
        } else {
            c.result = (Result.succeed(obj));
        }
    }
}
