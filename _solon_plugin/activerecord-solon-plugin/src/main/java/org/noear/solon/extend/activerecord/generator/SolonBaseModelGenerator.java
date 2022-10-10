package org.noear.solon.extend.activerecord.generator;

import java.util.Random;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;

/**
 * Base Model 代码生成器
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10.7
 */
public class SolonBaseModelGenerator extends BaseModelGenerator {

    protected MetaBuilder metaBuilder;

    protected Dialect dialect = null;

    protected String baseModelClassName = Model.class.getName();

    protected String baseModelSimpleName = Model.class.getSimpleName();

    public SolonBaseModelGenerator(DataSource dataSource, String baseModelPackageName, String baseModelOutputDir) {
        super(baseModelPackageName, baseModelOutputDir);

        this.template = "/org/noear/solon/extend/activerecord/generator/base_model_template.tp";
        this.metaBuilder = new MetaBuilder(dataSource);
    }

    public SolonBaseModelGenerator addExcludedTable(String... excludedTables) {
        this.metaBuilder.addExcludedTable(excludedTables);
        return this;
    }

    public SolonBaseModelGenerator addWhitelist(String... tableNames) {
        if (tableNames != null) {
            this.metaBuilder.addWhitelist(tableNames);
        }
        return this;
    }

    public void generate() {
        if (this.dialect != null) {
            this.metaBuilder.setDialect(this.dialect);
        }

        System.out.println("Generate BaseModel ...");
        System.out.println("BaseModel Output Dir: " + this.baseModelOutputDir);

        this.engine.addSharedMethod(new Random());
        this.engine.addSharedObject("baseModelClassName", this.baseModelClassName);
        this.engine.addSharedObject("baseModelSimpleName", this.baseModelSimpleName);

        super.generate(this.metaBuilder.build());
    }

    @Override
    protected void initEngine() {
        this.getterTypeMap.put("java.math.BigInteger", "getBigInteger");
        super.initEngine();
    }

    public SolonBaseModelGenerator setBaseModelClass(Class<? extends Model<?>> clz) {
        return this.setBaseModelClassName(clz.getName());
    }

    public SolonBaseModelGenerator setBaseModelClassName(String baseModelClassName) {
        this.baseModelClassName = baseModelClassName;
        int index = this.baseModelClassName.lastIndexOf(".");
        this.baseModelSimpleName = this.baseModelClassName.substring(index + 1);

        return this;
    }

    /**
     * 设置数据库方言，默认为 MysqlDialect
     */
    public SolonBaseModelGenerator setDialect(Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    public SolonBaseModelGenerator setGenerateRemarks(boolean generateRemarks) {
        this.metaBuilder.setGenerateRemarks(generateRemarks);
        return this;
    }

    /**
     * 设置需要被移除的表名前缀 例如表名 "tb_account"，移除前缀 "tb_" 后变为 "account"
     */
    public SolonBaseModelGenerator setRemovedTableNamePrefixes(String... prefixes) {
        this.metaBuilder.setRemovedTableNamePrefixes(prefixes);
        return this;
    }

}
