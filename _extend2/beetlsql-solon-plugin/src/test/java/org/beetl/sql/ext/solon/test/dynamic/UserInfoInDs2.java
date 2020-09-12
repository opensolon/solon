package org.beetl.sql.ext.solon.test.dynamic;

import lombok.Data;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.annotation.entity.TargetSQLManager;

@Table(name="user")
//@TargetSQLManager("sqlManager2")
@Data
public class UserInfoInDs2 {
    @AssignID
    private Integer id;
    private String name;

}
