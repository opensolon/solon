package features.validation;

import org.junit.jupiter.api.Test;
import org.noear.solon.validation.ValidUtils;
import org.noear.solon.validation.annotation.AssertTrue;

/**
 * @author noear 2025/12/31 created
 */
public class AssertTrueTest {
    @Test
    public void case1_fieldValidation() {
        boolean isOk;
        DemoWithField demo = new DemoWithField();
        demo.accepted = false;

        try {
            ValidUtils.validateEntity(demo);
            isOk = false;
        } catch (Exception e) {
            e.printStackTrace();
            isOk = e.getMessage().contains("@AssertTrue");
        }

        assert isOk;
    }

    @Test
    public void case2_fieldValidationSuccess() {
        DemoWithField demo = new DemoWithField();
        demo.accepted = true;

        ValidUtils.validateEntity(demo);
        assert true;
    }

    @Test
    public void case3_methodValidation() {
        boolean isOk;
        DemoWithMethod demo = new DemoWithMethod(false);

        try {
            ValidUtils.validateEntity(demo);
            isOk = false;
        } catch (Exception e) {
            e.printStackTrace();
            isOk = e.getMessage().contains("isValid must return true");
        }

        assert isOk;
    }

    @Test
    public void case4_methodValidationSuccess() {
        DemoWithMethod demo = new DemoWithMethod(true);

        ValidUtils.validateEntity(demo);
        assert true;
    }

    @Test
    public void case5_bothFieldAndMethod() {
        DemoWithBoth demo = new DemoWithBoth();
        demo.fieldAccepted = true;

        ValidUtils.validateEntity(demo);
        assert true;
    }

    @Test
    public void case6_methodValidationFailure() {
        boolean isOk;
        DemoWithBoth demo = new DemoWithBoth();
        demo.fieldAccepted = true;
        demo.methodValue = false;

        try {
            ValidUtils.validateEntity(demo);
            isOk = false;
        } catch (Exception e) {
            e.printStackTrace();
            isOk = e.getMessage().contains("@AssertTrue");
        }

        assert isOk;
    }

    public static class DemoWithField {
        @AssertTrue
        public Boolean accepted;
    }

    public static class DemoWithMethod {
        private final boolean value;

        public DemoWithMethod(boolean value) {
            this.value = value;
        }

        @AssertTrue(message = "isValid must return true")
        public boolean isValid() {
            return value;
        }
    }

    public static class DemoWithBoth {
        @AssertTrue
        public Boolean fieldAccepted = true;

        public boolean methodValue = true;

        @AssertTrue
        public boolean isMethodValid() {
            return methodValue;
        }
    }
}
