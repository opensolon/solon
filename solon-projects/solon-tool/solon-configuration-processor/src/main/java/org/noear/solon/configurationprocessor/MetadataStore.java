/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * copy from org.springframework.boot.configurationprocessor.MetadataStore
 */

package org.noear.solon.configurationprocessor;

import org.noear.solon.configurationprocessor.metadata.ConfigurationMetadata;
import org.noear.solon.configurationprocessor.metadata.InvalidConfigurationMetadataException;
import org.noear.solon.configurationprocessor.metadata.JsonMarshaller;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.*;

/**
 * A {@code MetadataStore} is responsible for the storage of metadata on the filesystem.
 *
 * @author Andy Wilkinson
 * @author Scott Frederick
 * @since 1.2.2
 */
public class MetadataStore {

    static final String METADATA_PATH = "META-INF/solon/solon-configuration-metadata.json";

    private static final String ADDITIONAL_METADATA_PATH = "META-INF/solon/additional-solon-configuration-metadata.json";

    private static final String RESOURCES_DIRECTORY = "resources";

    private static final String CLASSES_DIRECTORY = "classes";

    private final ProcessingEnvironment environment;

    public MetadataStore(ProcessingEnvironment environment) {
        this.environment = environment;
    }

    public ConfigurationMetadata readMetadata() {
        try {
            return readMetadata(getMetadataResource().openInputStream());
        } catch (IOException ex) {
            return null;
        }
    }

    public void writeMetadata(ConfigurationMetadata metadata) throws IOException {
        if (!metadata.getItems().isEmpty()) {
            try (OutputStream outputStream = createMetadataResource().openOutputStream()) {
                new JsonMarshaller().write(metadata, outputStream);
            }
        }
    }

    public ConfigurationMetadata readAdditionalMetadata() throws IOException {
        return readMetadata(getAdditionalMetadataStream());
    }

    private ConfigurationMetadata readMetadata(InputStream in) {
        try {
            return new JsonMarshaller().read(in);
        } catch (IOException ex) {
            return null;
        } catch (Exception ex) {
            throw new InvalidConfigurationMetadataException(
                    "Invalid additional meta-data in '" + METADATA_PATH + "': " + ex.getMessage(),
                    Diagnostic.Kind.ERROR);
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                //ignore
            }

        }
    }

    private FileObject getMetadataResource() throws IOException {
        return this.environment.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", METADATA_PATH);
    }

    private FileObject createMetadataResource() throws IOException {
        return this.environment.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", METADATA_PATH);
    }

    private InputStream getAdditionalMetadataStream() throws IOException {
        // Most build systems will have copied the file to the class output location
        FileObject fileObject = this.environment.getFiler()
                .getResource(StandardLocation.CLASS_OUTPUT, "", ADDITIONAL_METADATA_PATH);
        InputStream inputStream = getMetadataStream(fileObject);
        if (inputStream != null) {
            return inputStream;
        }
        try {
            File file = locateAdditionalMetadataFile(new File(fileObject.toUri()));
            return (file.exists() ? new FileInputStream(file) : fileObject.toUri().toURL().openStream());
        } catch (Exception ex) {
            throw new FileNotFoundException();
        }
    }

    private InputStream getMetadataStream(FileObject fileObject) {
        try {
            return fileObject.openInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    File locateAdditionalMetadataFile(File standardLocation) throws IOException {
        if (standardLocation.exists()) {
            return standardLocation;
        }
        String locations = this.environment.getOptions()
                .get(ConfigurationMetadataAnnotationProcessor.ADDITIONAL_METADATA_LOCATIONS_OPTION);
        if (locations != null) {
            for (String location : locations.split(",")) {
                File candidate = new File(location, ADDITIONAL_METADATA_PATH);
                if (candidate.isFile()) {
                    return candidate;
                }
            }
        }
        return new File(locateGradleResourcesDirectory(standardLocation), ADDITIONAL_METADATA_PATH);
    }

    private File locateGradleResourcesDirectory(File standardAdditionalMetadataLocation) throws FileNotFoundException {
        String path = standardAdditionalMetadataLocation.getPath();
        int index = path.lastIndexOf(CLASSES_DIRECTORY);
        if (index < 0) {
            throw new FileNotFoundException();
        }
        String buildDirectoryPath = path.substring(0, index);
        File classOutputLocation = standardAdditionalMetadataLocation.getParentFile().getParentFile();
        return new File(buildDirectoryPath, RESOURCES_DIRECTORY + '/' + classOutputLocation.getName());
    }

}
