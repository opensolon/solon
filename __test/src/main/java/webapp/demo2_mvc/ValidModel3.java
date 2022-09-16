package webapp.demo2_mvc;

import lombok.Data;
import org.noear.solon.validation.annotation.*;

import java.util.List;


/**
 * @author noear 2021/5/11 created
 */
@Data
public class ValidModel3 {
    @Date
    private String field1;

    @NotEmpty
    @Date
    private String field10;

    @Email
    private String field2;


    @NotEmpty
    @Email
    private String field20;

    @Pattern("\\d{3}-\\d+")
    private String field3;

    @NotEmpty
    @Pattern("\\d{3}-\\d+")
    private String field30;
}
