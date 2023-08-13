package graphql.solon.exception;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class MissingSchemaException extends RuntimeException {

    public MissingSchemaException() {
        super("No GraphQL schema definition was configured.");
    }
}
