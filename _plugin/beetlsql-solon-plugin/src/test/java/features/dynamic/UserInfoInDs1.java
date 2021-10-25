package features.dynamic;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.TargetSQLManager;

@Table(name="user")
@TargetSQLManager("ds1")
@Data
public class UserInfoInDs1 {
    @AssignID
    private Integer id;
    private String name;

}
