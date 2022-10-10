package org.noear.solon.extend.activerecord.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.noear.solon.Solon;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

/**
 * SQL 代码生成器，与 Dao 代码生成器配套使用
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10.7
 */
public class SolonSqlGenerator extends BaseModelGenerator {
    private String modelPacket;
    private String basePackage;
    private String classSuffix = "Mapper";
    private String classPrefix = "";
    private String engineName = "forSql" + new Random().nextInt();

    private MetaBuilder metaBuilder;

    public SolonSqlGenerator(DataSource dataSource, String sqlPackageName, String baseOutputDir, String modelPacket) {
        super(sqlPackageName, baseOutputDir);

        this.modelPacket = modelPacket;

        this.template = "/org/noear/solon/extend/activerecord/generator/sql_template.tp";
        this.metaBuilder = new MetaBuilder(dataSource);
    }

    public SolonSqlGenerator addExcludedTable(String... excludedTables) {
        this.metaBuilder.addExcludedTable(excludedTables);
        return this;
    }

    public SolonSqlGenerator addWhitelist(String... tableNames) {
        if (tableNames != null) {
            this.metaBuilder.addWhitelist(tableNames);
        }
        return this;
    }

    @Override
    protected void genBaseModelContent(TableMeta tableMeta) {
        Kv data = Kv.by("baseModelPackageName", this.baseModelPackageName);
        data.set("generateChainSetter", this.generateChainSetter);
        data.set("tableMeta", tableMeta);
        data.set("modelPacket", this.modelPacket);
        data.set("basePackage", this.basePackage);
        data.set("classSuffix", this.classSuffix);
        data.set("classPrefix", this.classPrefix);

        Engine engine = Engine.use(this.engineName);
        tableMeta.baseModelContent = engine.getTemplate(this.template).renderToString(data);
    }

    public void generate() {
        this.generate(this.metaBuilder.build());
    }

    @Override
    public void generate(List<TableMeta> tableMetas) {
        System.out.println("Generate Sql ...");
        System.out.println("Sql Output Dir: " + this.baseModelOutputDir);

        Engine engine = Engine.create(this.engineName);
        engine.setSourceFactory(new ClassPathSourceFactory());
        engine.addSharedMethod(new StrKit());
        engine.addSharedObject("getterTypeMap", this.getterTypeMap);
        engine.addSharedObject("javaKeyword", this.javaKeyword);

        for (TableMeta tableMeta : tableMetas) {
            this.genBaseModelContent(tableMeta);
        }
        this.writeToFile(tableMetas);
    }

    public String getClassPrefix() {
        return this.classPrefix;
    }

    public String getClassSuffix() {
        return this.classSuffix;
    }

    public SolonSqlGenerator setClassPrefix(String classPrefix) {
        this.classPrefix = classPrefix;
        return this;
    }

    public SolonSqlGenerator setClassSuffix(String classSuffix) {
        this.classSuffix = classSuffix;
        return this;
    }

    /**
     * 设置需要被移除的表名前缀 例如表名 "tb_account"，移除前缀 "tb_" 后变为 "account"
     */
    public SolonSqlGenerator setRemovedTableNamePrefixes(String... prefixes) {
        this.metaBuilder.setRemovedTableNamePrefixes(prefixes);
        return this;
    }


    /**
     * base model 覆盖写入
     */
    @Override
    protected void writeToFile(TableMeta tableMeta) throws IOException {
        File dir = new File(this.baseModelOutputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String target = this.baseModelOutputDir + File.separator + this.getClassPrefix() + tableMeta.modelName
            + this.classSuffix + ".sql";

        File targetFile = new File(target);
        if (targetFile.exists()) {
            return;
        }

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(target), Solon.encoding())) {
            osw.write(tableMeta.baseModelContent);
        }

    }
}
