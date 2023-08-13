package graphql.solon.resource;

import java.util.Map;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class GraphqlRequestParam {

    private String query;
    private String operationName;
    private Map<String, Object> variables;
    private Map<String, Object> extensions;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
}
