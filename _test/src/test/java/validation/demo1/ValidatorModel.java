package validation.demo1;

import lombok.Data;
import javax.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author noear 2021/5/10 created
 */
@Data
public class ValidatorModel {

    @NotBlank(message = "姓名不能为空")
    private String name;

    @Size(min = 3, max = 6)
    private List<String> friendNames;

    @Min(value = 18, message = "年龄未满18岁")
    @Max(value = 60, message = "年龄必须在60岁以下")
    private Integer age;

    private String tel;

    @DecimalMin(value = "30", message = "金钱不能小于30")
    private BigDecimal money;
}