package features;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;

@Table(name="user")
@Data
public class UserInfo {
    @AssignID
    private Long id;
    private String name;
}