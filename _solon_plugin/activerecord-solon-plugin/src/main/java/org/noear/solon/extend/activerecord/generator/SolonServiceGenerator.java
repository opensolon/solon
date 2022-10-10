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
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

/**
 * Dao 代码生成器
 *
 * @author 胡高 (https://gitee.com/gollyhu)
 * @since 1.10.7
 */
public class SolonServiceGenerator extends BaseModelGenerator {
    private String modelPacket;
    private String basePackage;
    private String classSuffix = "Service";
    private String classPrefix = "";
    private String engineName = "forService" + new Random().nextInt();

    private String baseServiceTemplate;

    private MetaBuilder metaBuilder;

    protected String baseModelClassName = Model.class.getName();

    protected String baseModelSimpleName = Model.class.getSimpleName();

    public SolonServiceGenerator(DataSource dataSource, String servicePackageName, String baseOutputDir, String modelPacket) {
        super(servicePackageName, baseOutputDir);

        this.modelPacket = modelPacket;

        this.template = "/org/noear/solon/extend/activerecord/generator/service_template.tp";
        this.baseServiceTemplate = "/org/noear/solon/extend/activerecord/generator/base_service_template.tp";
        this.metaBuilder = new MetaBuilder(dataSource);
    }

    public SolonServiceGenerator addExcludedTable(String... excludedTables) {
        this.metaBuilder.addExcludedTable(excludedTables);
        return this;
    }

    public SolonServiceGenerator addWhitelist(String... tableNames) {
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
        Engine engine = Engine.create(this.engineName);
        engine.setSourceFactory(new ClassPathSourceFactory());
        engine.addSharedMethod(new StrKit());
        engine.addSharedObject("getterTypeMap", this.getterTypeMap);
        engine.addSharedObject("javaKeyword", this.javaKeyword);
        engine.addSharedObject("baseModelClassName", this.baseModelClassName);
        engine.addSharedObject("baseModelSimpleName", this.baseModelSimpleName);

        this.generateBaseService(engine);
        this.generate(this.metaBuilder.build());
    }

    @Override
    public void generate(List<TableMeta> tableMetas) {
        System.out.println("Generate Service ...");
        System.out.println("Service Output Dir: " + this.baseModelOutputDir);

        for (TableMeta tableMeta : tableMetas) {
            this.genBaseModelContent(tableMeta);
        }
        this.writeToFile(tableMetas);
    }

    private void generateBaseService(Engine engine) {
        Kv data = Kv.by("baseModelPackageName", this.baseModelPackageName);
        data.set("generateChainSetter", this.generateChainSetter);
        data.set("modelPacket", this.modelPacket);
        data.set("basePackage", this.basePackage);
        data.set("classSuffix", this.classSuffix);
        data.set("classPrefix", this.classPrefix);

        String baseProviderTemplate = engine.getTemplate(this.baseServiceTemplate).renderToString(data);

        try {
            this.writebaseService(baseProviderTemplate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getClassPrefix() {
        return this.classPrefix;
    }

    public String getClassSuffix() {
        return this.classSuffix;
    }

    public void setBaseModelClassName(String baseModelClassName) {
        this.baseModelClassName = baseModelClassName;
        int index = this.baseModelClassName.lastIndexOf(".");
        this.baseModelSimpleName = this.baseModelClassName.substring(index + 1);
    }

    public SolonServiceGenerator setClassPrefix(String classPrefix) {
        this.classPrefix = classPrefix;
        return this;
    }

    public SolonServiceGenerator setClassSuffix(String classSuffix) {
        this.classSuffix = classSuffix;
        return this;
    }

    /**
     * 设置需要被移除的表名前缀 例如表名 "tb_account"，移除前缀 "tb_" 后变为 "account"
     */
    public SolonServiceGenerator setRemovedTableNamePrefixes(String... prefixes) {
        this.metaBuilder.setRemovedTableNamePrefixes(prefixes);
        return this;
    }

    private void writebaseService(String baseServiceTemplate) throws IOException {
        File dir = new File(this.baseModelOutputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String target = this.baseModelOutputDir + File.separator + this.getClassPrefix() + "BaseService.java";

        File targetFile = new File(target);
        if (targetFile.exists()) {
            return;
        }

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(target), Solon.encoding())) {
            osw.write(baseServiceTemplate);
        }
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
            + this.classSuffix + ".java";

        File targetFile = new File(target);
        if (targetFile.exists()) {
            return;
        }

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(target), Solon.encoding())) {
            osw.write(tableMeta.baseModelContent);
        }
    }
}
