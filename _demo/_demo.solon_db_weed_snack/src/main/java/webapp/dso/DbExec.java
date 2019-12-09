package webapp.dso;

import org.noear.weed.DbContext;
import org.noear.weed.SQLBuilder;
import org.noear.weed.xml.XmlSqlBlock;
import org.noear.weed.xml.XmlSqlFactory;

import java.util.HashMap;
import java.util.Map;

public class DbExec {
    private static DbContext master;
    private static DbContext slave;

    public static void init(DbContext masterDb, DbContext slaveDb){
        master = masterDb;
        slave = slaveDb;
    }

    /**
     * 执行：insert,update,delete
     *
     * @param sqlid =@{包名}.id
     * @return 自增ID或影响行数
     *
     * 示例1：//将userModel做为m变量在xmlsql里用
     * long id = DbExec.execute("@xxx.user_add",new DbMap().add("m",userModel).add("type",2));
     *
     * 示例2：//将各变量依次设置
     * long id = DbExec.execute("@xxx.user_add",new DbMap().add("name",name).add("sex",1));
     *
     * 示例3：//将userModel的字段分散为变量
     * long id = DbExec.execute("@xxx.user_add",new DbMap(userModel));
     *
     * */
    public static long execute(String sqlid, DbMap paramS) throws Exception{
        //
        //@开头，是为了在感观上和其它字符串有区别
        //
        String name = sqlid.substring(1);

        XmlSqlBlock block = XmlSqlFactory.get(name);
        if(block == null){
            throw new RuntimeException("Xml sql @" +name);
        }

        SQLBuilder s = block.builder.build(paramS);
        String sql = " "+s.toString().toLowerCase();

        if(sql.indexOf(" insert into ")>=0) {
            return master.sql(s).insert();
        }else{
            return master.sql(s).execute();
        }
    }

    /**
     * 批量执行：insert,update,delete
     *
     * @return 返回与sqlid对应的结果Map
     * */
    public static Map<String,Long> executeBatch(Map<String, DbMap> sqlidAndParmaSList) throws Exception{
        Map<String,Long> map = new HashMap<>();
        for(Map.Entry<String, DbMap> sp : sqlidAndParmaSList.entrySet()){
            long rst = execute(sp.getKey(),sp.getValue());
            map.put(sp.getKey(),rst);
        }
        return map;
    }
}
