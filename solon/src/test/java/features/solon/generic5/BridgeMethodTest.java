package features.solon.generic5;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.wrap.ClassWrap;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

/**
 * @author noear 2025/5/3 created
 */
public class BridgeMethodTest {
    @Test
    public void case1() {
        ClassWrap classWrap = ClassWrap.get(SysResourcePermissionController.class);

        assert classWrap.getDeclaredMethods().length == 0;

        assert classWrap.getMethods()[0].getName().equals("saveAll");
        assert classWrap.getMethods()[0].getGenericReturnType() instanceof ParameterizedType;
        assert classWrap.getMethods()[1].getName().equals("saveOne");
        assert classWrap.getMethods()[1].getGenericReturnType() instanceof TypeVariable;

        System.out.println(Arrays.toString(SysResourcePermissionController.class.getDeclaredMethods()));
        //=> [public java.util.List com.example.demo.App$SysResourcePermissionController.saveAll(java.util.List)]
    }

    public class SysResourcePermissionController extends BaseController<SysResourcePermissionService, SysResourcePermission, SysResourcePermissionId> {

    }

    abstract class BaseController<S extends BaseService<T, ID>, T, ID> {
        protected S service;

        public T saveOne(T ts) {
            return service.saveOne(ts);
        }

        public List<T> saveAll(List<T> ts) {
            return service.saveAll(ts);
        }
    }

    public class SysResourcePermissionService extends BaseService<SysResourcePermission, SysResourcePermissionId> {
    }

    abstract class BaseService<T, ID> {
        public T saveOne(T ts) {
            return ts;
        }

        public List<T> saveAll(List<T> ts) {
            return ts;
        }
    }

    public class SysResourcePermissionId {
    }

    public class SysResourcePermission {
        public int id;
        public String name;
    }
}