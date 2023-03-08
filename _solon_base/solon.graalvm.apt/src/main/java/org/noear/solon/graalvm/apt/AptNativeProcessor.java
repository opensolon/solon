package org.noear.solon.graalvm.apt;

import com.google.auto.service.AutoService;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.annotation.SolonMain;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import java.io.IOException;
import java.io.Writer;
import java.util.*;


/**
 * @author noear
 * @since 2.2
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"*"})
public class AptNativeProcessor extends AbstractProcessor {

    static Options jsonOptions = Options.def().add(Feature.PrettyFormat);

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(SolonMain.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!annotations.isEmpty()) {
            try {
                generateCode(roundEnv.getElementsAnnotatedWith(SolonMain.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    /**
     * 生成代码
     *
     * @param elements
     */
    private void generateCode(Set<? extends Element> elements) throws IOException {
        if (elements == null || elements.size() == 0) {
            return;
        }

        TypeElement typeElement = (TypeElement) elements.stream().findFirst().get();

        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(typeElement);
        String packageName = packageElement.getQualifiedName().toString();

        //添加 resource-config.json
        addResourceConfig(packageName);
        //添加 reflect-config.json
        addReflectConfig(packageName);
    }

    /**
     * 添加 resource-config.json
     */
    private void addResourceConfig(String packageName) throws IOException {
        String dir = "META-INF/native-image/" + packageName.replace(".", "/");
        String fileName = String.join("/", dir, "resource-config.json");

        FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT,
                "",
                fileName);

        ONode oNode = new ONode(jsonOptions);
        ONode includesNode = oNode.getOrNew("resources").getOrNew("includes").asArray();
        includesNode.addNew().set("pattern", "app.*\\.yml");
        includesNode.addNew().set("pattern", "app.*\\.properties");
        includesNode.addNew().set("pattern", "META-INF");
        includesNode.addNew().set("pattern", "META-INF/solon");
        includesNode.addNew().set("pattern", "META-INF/solon/.*");
        includesNode.addNew().set("pattern", "META-INF/solon_def/.*");

        try (Writer writer = fileObject.openWriter()) {
            writer.write(oNode.toJson());
        }
    }

    /**
     * 添加 reflect-config.json
     */
    private void addReflectConfig(String packageName) throws IOException {
        String dir = "META-INF/native-image/" + packageName.replace(".", "/");
        String fileName = String.join("/", dir, "reflect-config.json");

        FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT,
                "",
                fileName);

        ONode oNode = new ONode(jsonOptions).asArray();

        oNode.addNew().build(n -> buildReflectNode(n,"org.noear.solon.extend.impl.PropsLoaderExt"));
        oNode.addNew().build(n -> buildReflectNode(n,"org.noear.solon.extend.impl.PropsConverterExt"));

        try (Writer writer = fileObject.openWriter()) {
            writer.write(oNode.toJson());
        }
    }

    private void buildReflectNode(ONode n, String name) {
        n.set("name", name)
                .getOrNew("methods").addNew()
                .set("name", "<init>")
                .getOrNew("parameterTypes").asArray();

    }
}
