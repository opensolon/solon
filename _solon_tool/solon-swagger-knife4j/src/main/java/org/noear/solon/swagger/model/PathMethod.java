package org.noear.solon.swagger.model;

import java.util.List;

/**
 * @author noear 2023/3/17 created
 */
public class PathMethod {
    public List<String> tags;
    public String summary;
    public String description;
    public String operationId;
    public List<String> consumes;
    public List<String> produces;
    public List<PathMethodSecurity> security;
    public List<PathMethodParameter> parameters;
}
