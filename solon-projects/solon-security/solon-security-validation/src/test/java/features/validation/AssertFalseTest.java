package features.validation;

import org.junit.jupiter.api.Test;
import org.noear.solon.validation.ValidUtils;
import org.noear.solon.validation.annotation.AssertFalse;

/**
 * @author noear 2025/12/31 created
 */
public class AssertFalseTest {
    @Test
    public void case1_fieldValidation() {
        boolean isOk;
        DemoWithField demo = new DemoWithField();
        demo.disabled = true;

        try {
            ValidUtils.validateEntity(demo);
            isOk = false;
        } catch (Exception e) {
            e.printStackTrace();
            isOk = e.getMessage().contains("@AssertFalse");
        }

        assert isOk;
    }

    @Test
    public void case2_fieldValidationSuccess() {
        DemoWithField demo = new DemoWithField();
        demo.disabled = false;

        ValidUtils.validateEntity(demo);
        assert true;
    }

    @Test
    public void case3_methodValidation() {
        boolean isOk;
        DemoWithMethod demo = new DemoWithMethod(true);

        try {
            ValidUtils.validateEntity(demo);
            isOk = false;
        } catch (Exception e) {
            e.printStackTrace();
            isOk = e.getMessage().contains("isDisabled must return false");
        }

        assert isOk;
    }

    @Test
    public void case4_methodValidationSuccess() {
        DemoWithMethod demo = new DemoWithMethod(false);

        ValidUtils.validateEntity(demo);
        assert true;
    }

    @Test
    public void case5_bothFieldAndMethod() {
        DemoWithBoth demo = new DemoWithBoth();
        demo.fieldDisabled = false;

        ValidUtils.validateEntity(demo);
        assert true;
    }

    @Test
    public void case6_methodValidationFailure() {
        boolean isOk;
        DemoWithBoth demo = new DemoWithBoth();
        demo.fieldDisabled = false;
        demo.methodValue = true;

        try {
            ValidUtils.validateEntity(demo);
            isOk = false;
        } catch (Exception e) {
            e.printStackTrace();
            isOk = e.getMessage().contains("@AssertFalse");
        }

        assert isOk;
    }

    public static class DemoWithField {
        @AssertFalse
        public Boolean disabled;
    }

    public static class DemoWithMethod {
        private final boolean value;

        public DemoWithMethod(boolean value) {
            this.value = value;
        }

        @AssertFalse(message = "isDisabled must return false")
        public boolean isDisabled() {
            return value;
        }
    }

    public static class DemoWithBoth {
        @AssertFalse
        public Boolean fieldDisabled = false;

        public boolean methodValue = false;

        @AssertFalse
        public boolean isMethodDisabled() {
            return methodValue;
        }
    }
}
