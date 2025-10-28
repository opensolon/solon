package features.solon.generic;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.util.EgggUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
        List<Type> tmp = EgggUtil.findGenericList(DemoService.class, ServiceImplEx.class);

        System.out.println(tmp);

        assert tmp.size() == 2;
        assert tmp.get(0) == DemoMapper.class;
        assert tmp.get(1) == Demo.class;
    }

    @Test
    public void case3() {
        List<Type> tmp = EgggUtil.findGenericList(DemoService.class, ServiceImpl.class);

        System.out.println(tmp);

        assert tmp.size() == 2;
        assert tmp.get(0) == DemoMapper.class;
        assert tmp.get(1) == Demo.class;
    }

    @Test
    public void case4() {
        List<Type> tmp = EgggUtil.findGenericList(DemoImpl.class, Map.class);
        System.out.println(tmp);

        assert tmp.size() == 2;
        assert tmp.get(0) == Integer.class;
        assert tmp.get(1) == String.class;

        tmp = EgggUtil.findGenericList(DemoImpl.class, IDemo.class);
        System.out.println(tmp);

        assert tmp.size() == 1;
        assert tmp.get(0) == Double.class;
    }

    @Test
    public void case5() {
        List<Type> tmp = EgggUtil.findGenericList(DemoHashImpl.class, Map.class);
        System.out.println(tmp);

        assert tmp.size() == 2;
        assert tmp.get(0) == Integer.class;
        assert tmp.get(1) == String.class;

        tmp = EgggUtil.findGenericList(DemoHashImpl.class, IDemo.class);
        System.out.println(tmp);

        assert tmp.size() == 1;
        assert tmp.get(0) == Double.class;
    }

    @Test
    public void case6() {
        List<Type> tmp = EgggUtil.findGenericList(UserMapperI.class, MapperI.class);
        System.out.println(tmp);

        assert tmp.size() == 1;
        assert tmp.get(0) == UserD.class;
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
