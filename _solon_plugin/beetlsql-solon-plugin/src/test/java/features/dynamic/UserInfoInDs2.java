package features.dynamic;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.TargetSQLManager;

@Table(name="user")
@TargetSQLManager("ds2")
@Data
public class UserInfoInDs2 {
    @AssignID
    private Integer id;
    private String name;

}
