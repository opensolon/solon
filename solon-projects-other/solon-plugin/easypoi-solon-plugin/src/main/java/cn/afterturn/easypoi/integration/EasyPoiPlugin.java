package cn.afterturn.easypoi.integration;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;

/**
 * 插件入口
 *
 * <code><pre>
 * @Controller
 * public class DemoController{
 *     @Mapping("test")
 *     public ModelAndView test(ModelAndView mv){
 *         ExportParams exportParams = null;
 *         mv.put(BigExcelConstants.PARAMS, exportParams);
 *
 *         return mv.view(BigExcelConstants.EASYPOI_BIG_EXCEL_VIEW);
 *     }
 * }
 * </pre></code>
 *
 * @author noear 2022/10/7 created
 **/
public class EasyPoiPlugin implements Plugin {

    @Override
    public void start(AopContext context) {
        if(context.cfg().getBool("easy.poi.base.enable",true) == false){
            return;
        }

        context.beanScan("cn.afterturn.easypoi");

        //注册视图渲染器
        RenderManager.mapping(".poi", new EasypoiRender());
    }
}
