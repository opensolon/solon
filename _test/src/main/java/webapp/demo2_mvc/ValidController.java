package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.validation.annotation.*;

@Valid
@Mapping("/demo2/valid")
@Controller
public class ValidController {
    @Mapping("date")
    public String date(@Date String val1) {
        return "OK";
    }

    @Mapping("date2")
    public String date2(@Date("yyyy-MM-dd") String val1) {
        return "OK";
    }

    @Mapping("dmax")
    public String dmax(@DecimalMax(10.0) double val1, @DecimalMax(10.0) double val2) {
        return "OK";
    }

    @Mapping("dmin")
    public String dmin(@DecimalMin(10.0) double val1, @DecimalMin(10.0) double val2) {
        return "OK";
    }

    @Mapping("email")
    public String email(@Email String val1) {
        return "OK";
    }


    @Mapping("email2")
    public String email2(@Email("\\w+\\@live.cn") String val1) {
        return "OK";
    }


    @Mapping("max")
    public String max(@Max(10) int val1, @Max(10) int val2) {
        return "OK";
    }

    @Mapping("min")
    public String min(@Min(10) int val1, @Min(10)int val2) {
        return "OK";
    }

    @NoRepeatSubmit
    @Mapping("nrs")
    public String nrs() {
        return "OK";
    }

    @NotBlank({"val1", "val2"})
    @Mapping("nblank")
    public String nblank(String val1, String val2) {
        return "OK";
    }

    @NotEmpty({"val1", "val2"})
    @Mapping("nempty")
    public String nempty(String val1, String val2) {
        return "OK";
    }

    @NotNull({"val1", "val2"})
    @Mapping("nnull")
    public String nnull(String val1, String val2) {
        return "OK";
    }


    @Null({"val1", "val2"})
    @Mapping("null")
    public String nullx(String val1, String val2) {
        return "OK";
    }

    @Mapping("patt")
    public String patt(@Pattern("\\d{3}-\\d+") String val1, @Pattern("\\d{3}-\\d+") String val2) {
        return "OK";
    }


    //这是基于 context 的验证体系
    @NotZero({"val1", "val2"})
    @Mapping("nzero")
    public String nzero(int val1, int val2) {
        return "OK";
    }

    //这也是基于 context 的验证体系
    @Mapping("size")
    public String size(@Length(min = 2, max = 5, message = "测试") String val1,
                       @Length(min = 2, max = 5, message = "测试") String val2) {
        return "OK";
    }

    //这是基于 bean 的验证体系
    @Mapping("bean")
    public String bean(@Validated ValidModel model) {
        return "OK";
    }


    //这是基于 bean 的验证体系
    @Mapping("beanlist")
    public String beanlist(@Validated ValidViewModel model) {
        return "OK";
    }
}
