package features.tool;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;
import org.noear.solon.configurationprocessor.ConfigurationMetadataAnnotationProcessor;
import org.noear.solon.configurationprocessor.json.JSONArray;
import org.noear.solon.configurationprocessor.json.JSONException;
import org.noear.solon.configurationprocessor.json.JSONObject;
import webapp.demoz_tool.DemoEnum;
import webapp.demoz_tool.DemoEnumWithValue;
import webapp.demoz_tool.DemoProps;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BindPropsProcessorTest {

    private static final String METADATA_PATH = "META-INF/solon/solon-configuration-metadata.json";

    @Test
    public void test_metadata_generate() throws IOException, JSONException {
        Path basePath = Paths.get("src/main/java");

        JavaFileObject demoProps = JavaFileObjects.forSourceLines(DemoProps.class.getCanonicalName(),
                Files.readAllLines(basePath.resolve("webapp/demoz_tool/DemoProps.java")));
        JavaFileObject demoEnum = JavaFileObjects.forSourceLines(DemoEnum.class.getCanonicalName(),
                Files.readAllLines(basePath.resolve("webapp/demoz_tool/DemoEnum.java")));
        JavaFileObject demoEnumWithValue = JavaFileObjects.forSourceLines(DemoEnumWithValue.class.getCanonicalName(),
                Files.readAllLines(basePath.resolve("webapp/demoz_tool/DemoEnumWithValue.java")));

        Compilation compilation = javac()
                .withProcessors(new ConfigurationMetadataAnnotationProcessor())
                .compile(demoProps, demoEnum, demoEnumWithValue);

        com.google.testing.compile.CompilationSubject.assertThat(compilation).succeeded();
        com.google.testing.compile.CompilationSubject.assertThat(compilation).generatedFile(StandardLocation.CLASS_OUTPUT, METADATA_PATH);

        Optional<JavaFileObject> metadataFile = compilation.generatedFile(StandardLocation.CLASS_OUTPUT, METADATA_PATH);
        assertTrue(metadataFile.isPresent());

        String metadataContent = metadataFile.get().getCharContent(false).toString();
        System.out.println(metadataContent);
        JSONObject metadata = new JSONObject(metadataContent);

        // Verify groups
        assertTrue(metadata.has("groups"));
        JSONArray groups = metadata.getJSONArray("groups");
        assertTrue(groups.length() >= 1);

        // Verify properties
        assertTrue(metadata.has("properties"));
        JSONArray properties = metadata.getJSONArray("properties");
        assertTrue(properties.length() >= 18);

        // Check specific properties
        checkProperty(metadata, "demo.booleanValue", "java.lang.Boolean");
        checkProperty(metadata, "demo.stringValue", "java.lang.String");
        checkProperty(metadata, "demo.stringListValue", "java.util.List<java.lang.String>");
    }

    private void checkProperty(JSONObject metadata, String propertyName, String expectedType) throws JSONException {
        JSONArray properties = metadata.getJSONArray("properties");
        boolean found = false;

        for (int i = 0; i < properties.length(); i++) {
            JSONObject prop = properties.getJSONObject(i);
            if (propertyName.equals(prop.getString("name"))) {
                assertEquals(expectedType, prop.getString("type"));
                found = true;
                break;
            }
        }

        assertTrue(found, "Property " + propertyName + " not found in metadata");
    }
}