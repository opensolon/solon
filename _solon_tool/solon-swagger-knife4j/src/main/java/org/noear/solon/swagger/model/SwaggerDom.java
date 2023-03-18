package org.noear.solon.swagger.model;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2023/3/17 created
 */
public class SwaggerDom {
    public String swagger;
    public Info info;
    public String host;
    public String basePath;
    public List<String> schemes;
    public ExternalDocs externalDocs;
    public Map<String, Model> definitions;
    public Map<String, SecurityModel> securityDefinitions;
    public List<Tag> tags;
    public List<Path> paths;

}
