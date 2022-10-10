package org.noear.solon.extend.activerecord.generator;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;


/**
 * 代码生成器
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10.7
 */
public class SolonGenerator {

    protected String[] blacklist;

    protected String[] whitelist;

    protected String basePackageName;

    protected String[] removedTableNamePrefixes;

    protected String baseOutputDir;

    protected String baseModelClassName = Model.class.getName();

    protected Dialect dialect = new MysqlDialect();

    protected DataSource dataSource;

    protected String dataSourceBeanName;


    public SolonGenerator(DataSource dataSource, String basePackageName) {
        this.dataSource = dataSource;
        this.basePackageName = basePackageName;
        this.baseOutputDir = PathKit.getRootClassPath() + "/../../src/main/java/" + basePackageName.replace(".", "/");
    }

    public void addBlacklist(String... tableNames) {
        this.blacklist = tableNames;
    }

    public void addWhitelist(String... tableNames) {
        this.whitelist = tableNames;
    }

    public void generate() {
        // 各存放的路径
        String baseModelDir = this.baseOutputDir + "/model/base";
        String modelDir = this.baseOutputDir + "/model";
        String mapperDir = this.baseOutputDir + "/mapper";
        String serviceDir = this.baseOutputDir + "/service";
        String providerDir = this.baseOutputDir + "/provider";
        String sqlMapperDir = PathKit.getRootClassPath() + "/../../src/main/resources/sql/";

        /*
         * 生成 BaseModel
         */
        SolonBaseModelGenerator baseModelGenerator =
            new SolonBaseModelGenerator(this.dataSource, this.basePackageName + ".model.base", baseModelDir);
        //baseModelGenerator.setGenerateChainSetter(true);
        baseModelGenerator.setGenerateRemarks(true);
        baseModelGenerator.setBaseModelClassName(this.baseModelClassName);
        baseModelGenerator.addExcludedTable(this.blacklist);
        baseModelGenerator.setRemovedTableNamePrefixes(this.removedTableNamePrefixes);

        baseModelGenerator.generate();

        /*
         * 生成 Model
         */
        SolonModelGenerator modelGenerator = new SolonModelGenerator(this.dataSource, this.basePackageName + ".model",
            this.basePackageName + ".model.base", modelDir);
        modelGenerator.setGenerateDaoInModel(true);
        modelGenerator.setDataSourceBeanName(this.dataSourceBeanName);
        modelGenerator.setDialect(this.dialect);
        modelGenerator.setGenerateRemarks(true);
        modelGenerator.addExcludedTable(this.blacklist);
        modelGenerator.setRemovedTableNamePrefixes(this.removedTableNamePrefixes);

        modelGenerator.generate();

        /*
         * 生成 Dao
         */
        SolonMapperGenerator daoGenerator = new SolonMapperGenerator(this.dataSource, this.basePackageName + ".mapper",
            mapperDir, this.basePackageName + ".model");
        daoGenerator.addExcludedTable(this.blacklist);
        daoGenerator.setRemovedTableNamePrefixes(this.removedTableNamePrefixes);

        daoGenerator.generate();

        /*
         * 生成 Sql
         */
        SolonSqlGenerator sqlGenerator = new SolonSqlGenerator(this.dataSource, this.basePackageName + ".mapper",
            sqlMapperDir, this.basePackageName + ".model");
        daoGenerator.addExcludedTable(this.blacklist);
        sqlGenerator.setRemovedTableNamePrefixes(this.removedTableNamePrefixes);

        sqlGenerator.generate();

        /*
         * 生成service接口
         */
        SolonServiceGenerator serviceGenerator = new SolonServiceGenerator(this.dataSource,
            this.basePackageName + ".service", serviceDir, this.basePackageName + ".model");
        serviceGenerator.addExcludedTable(this.blacklist);
        serviceGenerator.setBaseModelClassName(this.baseModelClassName);
        serviceGenerator.setRemovedTableNamePrefixes(this.removedTableNamePrefixes);

        serviceGenerator.generate();

        /*
         * 生成provider类
         */
        SolonProviderGenerator providerGenerator = new SolonProviderGenerator(this.dataSource,
            this.basePackageName + ".provider", providerDir, this.basePackageName);
        providerGenerator.addExcludedTable(this.blacklist);
        providerGenerator.setRemovedTableNamePrefixes(this.removedTableNamePrefixes);
        providerGenerator.setDataSourceBeanName(this.dataSourceBeanName);
        providerGenerator.setBaseModelClassName(this.baseModelClassName);

        providerGenerator.generate();

    }

    public void setBaseModelClassName(String baseModelClassName) {
        this.baseModelClassName = baseModelClassName;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSourceBeanName(String name) {
        this.dataSourceBeanName = name;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public void setRemovedTableNamePrefixes(String... prefixes) {
        this.removedTableNamePrefixes = prefixes;
    }
}
