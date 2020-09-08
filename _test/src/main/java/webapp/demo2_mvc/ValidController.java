package webapp.demo2_mvc;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.validation.annotation.*;

@XMapping("/demo2/valid")
@XValid
@XController
public class ValidController {
    @XMapping("dmax")
    public String dmax(@DecimalMax(10.0) double val1, @DecimalMax(10.0) double val2) {
        return "OK";
    }

    @XMapping("dmin")
    public String dmin(@DecimalMin(10.0) double val1, @DecimalMin(10.0) double val2) {
        return "OK";
    }

    @XMapping("max")
    public String max(@Max(10) int val1, @Max(10) int val2) {
        return "OK";
    }

    @XMapping("min")
    public String min(@Min(10) int val1, @Min(10)int val2) {
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

    @XMapping("patt")
    public String patt(@Pattern("\\d{3}-\\d+") String val1, @Pattern("\\d{3}-\\d+") String val2) {
        return "OK";
    }

    @XMapping("size")
    public String size(@Size(min = 2, max = 5, message = "测试") String val1,
                       @Size(min = 2, max = 5, message = "测试") String val2) {
        return "OK";
    }
}
