package org.noear.solon.extend.activerecord.generator;

import java.util.Random;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.ModelGenerator;

/**
 * Model 代码生成器
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10.7
 */
public class SolonModelGenerator extends ModelGenerator {

    protected MetaBuilder metaBuilder;

    protected Dialect dialect = null;
    protected String dataSourceBeanName = null;

    public SolonModelGenerator(DataSource dataSource, String modelPackageName, String baseModelPackageName, String modelOutputDir) {
        super(modelPackageName, baseModelPackageName, modelOutputDir);

        this.template = "/org/noear/solon/extend/activerecord/generator/model_template.tp";
        this.metaBuilder = new MetaBuilder(dataSource);

    }

    public SolonModelGenerator addExcludedTable(String... excludedTables) {
        this.metaBuilder.addExcludedTable(excludedTables);
        return this;
    }

    // 增加白名单功能
    public SolonModelGenerator addWhitelist(String... tableNames) {
        if (tableNames != null) {
            this.metaBuilder.addWhitelist(tableNames);
        }
        return this;
    }

    public void generate() {
        if (this.dialect != null) {
            this.metaBuilder.setDialect(this.dialect);
        }
        System.out.println("Generate Model ...");
        System.out.println("Model Output Dir: " + this.modelOutputDir);

        this.engine.addSharedMethod(new Random());
        this.engine.addSharedObject("dataSourceBeanName", this.dataSourceBeanName);
        super.generate(this.metaBuilder.build());
    }

    public SolonModelGenerator setDataSourceBeanName(String name) {
        this.dataSourceBeanName = name;
        return this;
    }

    /**
     * 设置数据库方言，默认为 MysqlDialect
     */
    public SolonModelGenerator setDialect(Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    public SolonModelGenerator setGenerateRemarks(boolean generateRemarks) {
        this.metaBuilder.setGenerateRemarks(generateRemarks);
        return this;
    }

    /**
     * 设置需要被移除的表名前缀 例如表名 "tb_account"，移除前缀 "tb_" 后变为 "account"
     */
    public SolonModelGenerator setRemovedTableNamePrefixes(String... prefixes) {
        this.metaBuilder.setRemovedTableNamePrefixes(prefixes);
        return this;
    }


}