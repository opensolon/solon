package org.noear.solon.extend.activerecord;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/5/24 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {

    }

    static String jdbcUrl = "jdbc:mysql://localhost/jfinal_demo?useSSL=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
    static String user = "root";
    static String password = "yourpassword";

    public static DruidPlugin createDruidPlugin() {
        DruidPlugin druidPlugin = new DruidPlugin(jdbcUrl, user, password);
        return druidPlugin;
    }

    public static void initActiveRecordPlugin() {
        DruidPlugin druidPlugin = createDruidPlugin();

        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        arp.setDevMode(true);
        arp.setShowSql(true);

        // 添加 sql 模板文件，实际开发时将 sql 文件放在 src/main/resources 下
        arp.addSqlTemplate("com/jfinal/plugin/activerecord/test.sql");

        // 所有映射在生成的 _MappingKit.java 中自动化搞定
        _MappingKit.mapping(arp);

        // 先启动 druidPlugin，后启动 arp
        druidPlugin.start();
        arp.start();
    }

    public static void main(String[] args) {
        initActiveRecordPlugin();

        // 使用 Model
        Blog dao = new Blog().dao();
        Blog blog = dao.template("findBlog", 1).findFirst();
        System.out.println(blog.getTitle());

        // 使用 Db + Record 模式
        Record record = Db.template("findBlog", 1).findFirst();
        System.out.println(record.getStr("title"));
    }
}
