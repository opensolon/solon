package org.noear.solon.graalvm.apt;

import com.google.auto.service.AutoService;

import org.noear.solon.annotation.SolonMain;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author noear
 * @since 2.2
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"*"})
public class NativeProcessor extends AbstractProcessor {

    Map<String, Class<? extends Annotation>> supportedAnnMap = new LinkedHashMap<>();

    public NativeProcessor(){
        super();
        supportedAnnMap.put(SolonMain.class.getCanonicalName(), SolonMain.class);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnMap.keySet();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
