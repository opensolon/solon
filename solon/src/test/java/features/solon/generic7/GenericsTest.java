package features.solon.generic7;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.util.TypeReference;

import java.util.List;

/**
 * @author noear 2025/6/3 created
 */
public class GenericsTest {
    @Test
    public void case1() {
        AppContext appContext = new AppContext();
        appContext.beanScan(Config.class);
        appContext.start();

        LoginKit loginKit1 = appContext.getBean(LoginKit.class);

        assert loginKit1 != null;


        LoginKit<?> loginKit2 = appContext.getBean(new TypeReference<LoginKit<LoginUser>>() {
        });

        assert loginKit2 != null;
    }


    @Test
    public void case2() {
        AppContext appContext = new AppContext();
        appContext.beanScan(Config.class);
        appContext.start();


        List<LoginKit<?>> loginKit3 = appContext.getBeansOfType(new TypeReference<LoginKit<?>>() {
        });

        assert loginKit3 != null;
        assert loginKit3.size() == 1;


        List<LoginKit<? extends LoginUser>> loginKit4 = appContext.getBeansOfType(new TypeReference<LoginKit<? extends LoginUser>>() {
        });

        assert loginKit4 != null;
        assert loginKit4.size() == 1;


        List<LoginKit<? extends Object>> loginKit5 = appContext.getBeansOfType(new TypeReference<LoginKit<? extends Object>>() {
        });

        assert loginKit5 != null;
        assert loginKit5.size() == 1;
    }

    @Test
    public void case3() {
        AppContext appContext = new AppContext();
        appContext.beanScan(Config.class);
        appContext.start();


        List<LoginKit<? super LoginUser>> loginKit6 = appContext.getBeansOfType(new TypeReference<LoginKit<? super LoginUser>>() {
        });

        assert loginKit6 != null;
        assert loginKit6.size() == 1;

        List<LoginKit<? super Object>> loginKit7 = appContext.getBeansOfType(new TypeReference<LoginKit<? super Object>>() {
        });

        assert loginKit7 != null;
        assert loginKit7.size() == 0;
    }
}