package graphql.solon.properties;

import java.util.LinkedList;
import java.util.List;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Inject(value = "${solon.plugin.graphql}", required = false)
@Configuration
public class GraphqlProperties {

    public static final String PREFIX = "solon.plugin.graphql";

    private Schema schema;

    public GraphqlProperties() {
        this.schema = new Schema();
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public static class Schema {

        /**
         * Locations of GraphQL schema files.
         */
        private List<String> locations;

        /**
         * File extensions for GraphQL schema files.
         */
        private List<String> fileExtensions;

        public Schema() {
            this.locations = new LinkedList<>();
            this.locations.add("graphql");
            this.fileExtensions = new LinkedList<>();
            this.fileExtensions.add(".graphqls");
            this.fileExtensions.add(".gqls");
        }

        public List<String> getLocations() {
            return locations;
        }

        public void setLocations(List<String> locations) {
            this.locations = locations;
        }

        public List<String> getFileExtensions() {
            return fileExtensions;
        }

        public void setFileExtensions(List<String> fileExtensions) {
            this.fileExtensions = fileExtensions;
        }
    }
}
