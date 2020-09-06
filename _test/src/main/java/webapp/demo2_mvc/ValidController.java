package webapp.demo2_mvc;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.validation.annotation.*;

@XMapping("/demo2/valid")
@XValid
@XController
public class ValidController {

    @DecimalMax(value = {"val1", "val2"}, max = 10.0)
    @XMapping("dmax")
    public String dmax(double val1, double val2) {
        return "OK";
    }

    @DecimalMin(value = {"val1", "val2"}, min = 10.0)
    @XMapping("dmin")
    public String dmin(double val1, double val2) {
        return "OK";
    }

    @Max(value = {"val1", "val2"}, max = 10)
    @XMapping("max")
    public String max(int val1, int val2) {
        return "OK";
    }

    @Min(value = {"val1", "val2"}, min = 10)
    @XMapping("min")
    public String min(int val1, int val2) {
        return "OK";
    }

    @NoRepeatSubmit
    @XMapping("nrs")
    public String nrs() {
        return "OK";
    }

    @NotBlank({"val1", "val2"})
    @XMapping("nblank")
    public String nblank(String val1, String val2) {
        return "OK";
    }

    @NotEmpty({"val1", "val2"})
    @XMapping("nempty")
    public String nempty(String val1, String val2) {
        return "OK";
    }

    @NotNull({"val1", "val2"})
    @XMapping("nnull")
    public String nnull(String val1, String val2) {
        return "OK";
    }

    @NotZero({"val1", "val2"})
    @XMapping("nzero")
    public String nzero(String val1, String val2) {
        return "OK";
    }

    @Null({"val1", "val2"})
    @XMapping("null")
    public String nullx(String val1, String val2) {
        return "OK";
    }

    @Pattern(value = {"val1", "val2"}, expr = "\\d{3}-\\d+")
    @XMapping("patt")
    public String patt(String val1, String val2) {
        return "OK";
    }

    @Size(value = {"val1", "val2"}, min = 2, max = 5, message = "测试")
    @XMapping("size")
    public String size(String val1, String val2) {
        return "OK";
    }
}
