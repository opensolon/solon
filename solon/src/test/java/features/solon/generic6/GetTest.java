package features.solon.generic6;

import org.junit.jupiter.api.Test;
import org.noear.eggg.ParameterizedTypeImpl;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.TypeReference;

import java.util.List;

/**
 * @author noear 2025/6/3 created
 */
public class GetTest {
    @Test
    public void case11() {
        AppContext context = new AppContext();

        BeanWrap bw = context.wrap(UserDataService.class);
        context.beanRegister(bw, null, true);
        DataService<User> bean = context.getBean(new ParameterizedTypeImpl(DataService.class, new Class[]{User.class}));

        assert bean != null;
        assert bean instanceof UserDataService;
    }

    @Test
    public void case12() {
        AppContext context = new AppContext();

        BeanWrap bw = context.wrap(UserDataService.class);
        context.beanRegister(bw, null, true);
        DataService<User> bean = context.getBean(new TypeReference<DataService<User>>() {
        });

        assert bean != null;
        assert bean instanceof UserDataService;
    }

    @Test
    public void case21() {
        AppContext context = new AppContext();

        BeanWrap bw = context.wrap(UserDataService.class);
        context.beanRegister(bw, null, true);
        List<DataService<User>> beanList = context.getBeansOfType(new TypeReference<DataService<User>>() {
        });

        assert beanList != null;
        assert beanList.size() == 1;
        assert beanList.get(0) instanceof UserDataService;
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