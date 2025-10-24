package features.solon.generic5;

import org.junit.jupiter.api.Test;
import org.noear.eggg.ClassEggg;
import org.noear.eggg.MethodEggg;
import org.noear.solon.core.util.EgggUtil;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

/**
 * @author noear 2025/5/3 created
 */
public class BridgeMethodTest {
    @Test
    public void case1() {
        ClassEggg classWrap = EgggUtil.getClassEggg(SysResourcePermissionController.class);

        assert classWrap.getDeclaredMethodEgggs().size() == 0;

        MethodEggg[] methodEgggs = classWrap.getPublicMethodEgggs().toArray(new MethodEggg[0]);
        assert methodEgggs[0].getName().equals("saveAll");
        assert methodEgggs[0].getReturnTypeEggg().getGenericType() instanceof ParameterizedType;
        assert methodEgggs[1].getName().equals("saveOne");
        assert methodEgggs[1].getReturnTypeEggg().getGenericType().equals(SysResourcePermission.class);

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