package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Condition;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Props;
import org.noear.solon.core.util.ConditionUtil;

import java.lang.annotation.Annotation;

/**
 * @author noear 2024/10/25 created
 */
public class ConditionUtilText {
    public AppContext getAppContext() {
        AppContext context = new AppContext(new Props());
        context.cfg().setProperty("prop1", "1");
        context.cfg().setProperty("prop2", "2");
        return context;
    }

    @Test
    public void test1() throws Exception {
        AppContext context = getAppContext();

        assert ConditionUtil.test(context, new PropertyCondition("${prop1}"));
        assert ConditionUtil.test(context, new PropertyCondition(" ${prop1} "));
        assert ConditionUtil.test(context, new PropertyCondition("prop1"));
        assert ConditionUtil.test(context, new PropertyCondition(" prop1 "));
    }

    @Test
    public void test2() throws Exception {
        AppContext context = getAppContext();


        assert ConditionUtil.test(context, new PropertyCondition("${prop1} = 1"));
        assert ConditionUtil.test(context, new PropertyCondition("${prop1} == 1"));
        assert ConditionUtil.test(context, new PropertyCondition("${prop1} = 2")) == false;
        assert ConditionUtil.test(context, new PropertyCondition("${prop1} == 2")) == false;
    }

    @Test
    public void test3() throws Exception {
        AppContext context = getAppContext();

        assert ConditionUtil.test(context, new PropertyCondition("${prop1} == 1"));
        assert ConditionUtil.test(context, new PropertyCondition("${prop1} != 1")) == false;
    }

    @Test
    public void test4() throws Exception {
        AppContext context = getAppContext();

        assert ConditionUtil.test(context, new PropertyCondition("${prop1} == 1 && ${prop2} == 2"));
        assert ConditionUtil.test(context, new PropertyCondition("${prop1} != 1 && ${prop2} == 2")) == false;
    }

    public static class PropertyCondition implements Condition {
        private String onProperty = "";

        public PropertyCondition(String onProperty) {
            this.onProperty = onProperty;
        }

        @Override
        public Class<?> onClass() {
            return null;
        }

        @Override
        public String onClassName() {
            return "";
        }

        @Override
        public String onProperty() {
            return onProperty;
        }

        @Override
        public String onExpression() {
            return "";
        }

        @Override
        public Class<?> onMissingBean() {
            return null;
        }

        @Override
        public String onMissingBeanName() {
            return "";
        }

        @Override
        public Class<?> onBean() {
            return null;
        }

        @Override
        public String onBeanName() {
            return "";
        }

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }
    }
}
