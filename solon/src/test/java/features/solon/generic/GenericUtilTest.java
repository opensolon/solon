package features.solon.generic;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.util.GenericUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2024/10/30 created
 */
public class GenericUtilTest {
    @Test
    public void case1() throws Exception {
        AppContext appContext = new AppContext();
        appContext.beanScan(GenericUtilTest.class);

        DemoService demoService = appContext.getBean(DemoService.class);
        boolean isOk = demoService.getBaseMapper() != null;

        System.out.println(isOk ? "ok" : "not ok");
    }

    @Test
    public void case2() {
        Class<?>[] tmp = GenericUtil.resolveTypeArguments(DemoService.class, ServiceImplEx.class);

        System.out.println(Arrays.toString(tmp));

        assert tmp.length == 2;
        assert tmp[0] == DemoMapper.class;
        assert tmp[1] == Demo.class;
    }

    @Test
    public void case3() {
        Class<?>[] tmp = GenericUtil.resolveTypeArguments(DemoService.class, ServiceImpl.class);

        System.out.println(Arrays.toString(tmp));

        assert tmp.length == 2;
        assert tmp[0] == DemoMapper.class;
        assert tmp[1] == Demo.class;
    }

    @Test
    public void case4() {
        Class<?>[] tmp = GenericUtil.resolveTypeArguments(DemoImpl.class, Map.class);
        System.out.println(Arrays.toString(tmp));

        assert tmp.length == 2;
        assert tmp[0] == Integer.class;
        assert tmp[1] == String.class;

        tmp = GenericUtil.resolveTypeArguments(DemoImpl.class, IDemo.class);
        System.out.println(Arrays.toString(tmp));

        assert tmp.length == 1;
        assert tmp[0] == Double.class;
    }

    @Test
    public void case5() {
        Class<?>[] tmp = GenericUtil.resolveTypeArguments(DemoHashImpl.class, Map.class);
        System.out.println(Arrays.toString(tmp));

        assert tmp.length == 2;
        assert tmp[0] == Integer.class;
        assert tmp[1] == String.class;

        tmp = GenericUtil.resolveTypeArguments(DemoHashImpl.class, IDemo.class);
        System.out.println(Arrays.toString(tmp));

        assert tmp.length == 1;
        assert tmp[0] == Double.class;
    }

    @Test
    public void case6() {
        Class<?>[] tmp = GenericUtil.resolveTypeArguments(UserMapperI.class, MapperI.class);
        System.out.println(Arrays.toString(tmp));

        assert tmp.length == 1;
        assert tmp[0] == UserD.class;
    }

    private interface IDemo<T> {
    }

    private abstract static class DemoImpl implements Map<Integer, String>, IDemo<Double> {

    }

    private abstract static class DemoHashImpl extends HashMap<Integer, String> implements IDemo<Double> {

    }

    private class UserD {

    }

    public interface MapperI<T> {
    }

    public interface UserMapperI extends MapperI<UserD> {
    }
}
