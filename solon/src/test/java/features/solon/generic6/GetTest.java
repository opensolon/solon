package features.solon.generic6;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.ParameterizedTypeImpl;
import org.noear.solon.core.util.TypeReference;

/**
 * @author noear 2025/6/3 created
 */
public class GetTest {
    @Test
    public void case1() {
        AppContext context = new AppContext();

        BeanWrap bw = context.wrap(UserDataService.class);
        context.beanRegister(bw, null, true);
        DataService<User> bean = context.getBean(new ParameterizedTypeImpl(DataService.class, new Class[]{User.class}));

        assert bean != null;
        assert bean instanceof UserDataService;
    }

    @Test
    public void case2() {
        AppContext context = new AppContext();

        BeanWrap bw = context.wrap(UserDataService.class);
        context.beanRegister(bw, null, true);
        DataService<User> bean = context.getBean(new TypeReference<DataService<User>>(){});

        assert bean != null;
        assert bean instanceof UserDataService;
    }

    public static class UserDataService implements DataService<User> {
        private User data;

        @Override
        public void setData(User data) {
            this.data = data;
        }

        @Override
        public User getData() {
            return data;
        }
    }

    public interface DataService<T> {
        void setData(T data);

        T getData();
    }

    public static class User {

    }
}