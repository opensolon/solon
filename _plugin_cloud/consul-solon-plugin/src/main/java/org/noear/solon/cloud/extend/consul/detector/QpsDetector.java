package org.noear.solon.cloud.extend.consul.detector;

import com.wujiuye.flow.FlowHelper;
import com.wujiuye.flow.FlowType;
import com.wujiuye.flow.Flower;
import org.noear.solon.SolonApp;

import java.util.HashMap;
import java.util.Map;

public class QpsDetector extends AbstractDetector{
    protected final FlowHelper flowHelper;
    public QpsDetector(FlowType ...types){
        this.flowHelper=new FlowHelper(types);
    }
    public QpsDetector(){
        this(FlowType.Second);
    }
    @Override
    public String getName() {
        return "qps";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String,Object> info=new HashMap<>();
        Flower flower=flowHelper.getFlow(FlowType.Second);
        /**
         *         System.out.println("总请求数:"+flower.total());
         *         System.out.println("成功请求数:"+flower.totalSuccess());
         *         System.out.println("异常请求数:"+flower.totalException());
         *         System.out.println("平均请求耗时:"+flower.avgRt());
         *         System.out.println("最大请求耗时:"+flower.maxRt());
         *         System.out.println("最小请求耗时:"+flower.minRt());
         *         System.out.println("平均请求成功数(每毫秒):"+flower.successAvg());
         *         System.out.println("平均请求异常数(每毫秒):"+flower.exceptionAvg());
         */
        info.put("total",flower.total());
        info.put("totalException",flower.totalException());
        info.put("totalSuccess",flower.totalSuccess());
        info.put("argRt",flower.avgRt());
        info.put("maxRt",flower.maxRt());
        info.put("minRt",flower.minRt());
        info.put("successAvg",flower.successAvg());
        info.put("exceptionAvg",flower.exceptionAvg());
        return info;
    }

    /**
     * 开始检测
     * @param solon
     */
    public void toDetect(SolonApp solon){
        solon.before("**", ctx->{
            ctx.attrSet("_begin_time",System.currentTimeMillis());
        });
        solon.after("**",ctx->{
            Long begin= ctx.attr("_begin_time");
            if(begin!=null){
                flowHelper.incrSuccess(System.currentTimeMillis()-begin);
            }
        });
        solon.onError(e->{
            flowHelper.incrException();
        });
    }
}
