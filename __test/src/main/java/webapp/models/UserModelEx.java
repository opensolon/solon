package webapp.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author noear 2022/5/18 created
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class UserModelEx extends UserModel{
    private String icon;
}
