package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.validation.annotation.*;

import java.util.List;
import java.util.Map;

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

    @Mapping("date3")
    public String date3(@NotEmpty @Date String val1) {
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

    @Mapping("email3")
    public String email3(@NotEmpty @Email String val1) {
        return "OK";
    }


    @Mapping("numeric")
    public String numeric(@Numeric String val1) {
        return "OK";
    }

    @Mapping("numeric2")
    public String numeric2(@NotEmpty @Numeric String val1) {
        return "OK";
    }


    @Mapping("max")
    public String max(@Max(10) int val1, @Max(10) int val2) {
        return "OK";
    }

    @Mapping("min")
    public String min(@Min(10) int val1, @Min(10) int val2) {
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

    @Mapping("nnull2")
    public String nnull2(@NotNull(message = "val1") Integer val1) {
        return "OK";
    }


    @Null({"val1", "val2"})
    @Mapping("null")
    public String nullx(String val1, String val2) {
        return "OK";
    }

    @Mapping("patt")
    public String patt(@Pattern(value = "\\d{3}-\\d+", message = "test") String val1, @Pattern(value = "\\d{3}-\\d+$", message = "demo") String val2) {
        return "OK";
    }

    @Mapping("patt2")
    public String patt2(@NotEmpty @Pattern("\\d{3}-\\d+") String val1, @NotEmpty @Pattern("\\d{3}-\\d+$") String val2) {
        return "OK";
    }


    //这是基于 context 的验证体系
    @NotZero({"val1", "val2"})
    @Mapping("nzero")
    public String nzero(int val1, int val2) {
        return "OK";
    }

    //这也是基于 context 的验证体系
    @Mapping("length")
    public String length(@Length(min = 2, max = 5, message = "测试") String val1,
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

    //这是基于 bean 的验证体系
    @Mapping("beanlist2")
    public String beanlist2(@Validated List<ValidModel> list) {
        return "OK";
    }

    //这是基于 bean 的验证体系
    @Mapping("map")
    public String map(@Validated Map<String, ValidModel> map) {
        return "OK";
    }

    //这是基于 bean 的验证体系
    @Mapping("bean2")
    public String bean2(@Validated ValidModel2 model) {
        return "OK";
    }

    //这是基于 bean 的验证体系
    @Mapping("bean3")
    public String bean3(@Validated ValidModel3 model) {
        return "OK";
    }
}
