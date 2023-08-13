package graphql.solon.execution;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.noear.solon.lang.Nullable;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class ClassNameTypeResolver implements TypeResolver {

    private Function<Class<?>, String> classNameExtractor = Class::getSimpleName;

    private final Map<Class<?>, String> mappings = new LinkedHashMap<>();


    /**
     * Customize how the name of a class, or a base class/interface, is determined. An application
     * can use this to adapt to a common naming convention, e.g. remove an "Impl" suffix or a "Base"
     * prefix, and so on.
     * <p>By default, this is just {@link Class#getSimpleName()}.
     *
     * @param classNameExtractor the function to use
     */
    public void setClassNameExtractor(Function<Class<?>, String> classNameExtractor) {
        if (Objects.isNull(classNameExtractor)) {
            throw new IllegalArgumentException("'classNameExtractor' is required");
        }
        this.classNameExtractor = classNameExtractor;
    }

    /**
     * Add a mapping from a Java {@link Class} to a GraphQL Object type name. The mapping applies to
     * the given type and to all of its sub-classes (for a base class) or implementations (for an
     * interface).
     *
     * @param clazz the Java class to map
     * @param graphQlTypeName the matching GraphQL object type
     */
    public void addMapping(Class<?> clazz, String graphQlTypeName) {
        this.mappings.put(clazz, graphQlTypeName);
    }


    @Override
    @Nullable
    public GraphQLObjectType getType(TypeResolutionEnvironment environment) {

        Class<?> clazz = environment.getObject().getClass();
        GraphQLSchema schema = environment.getSchema();

        // We don't assert "not null" since GraphQL Java will do that anyway.
        // Leaving the method nullable provides option for delegation.

        return getTypeForClass(clazz, schema);
    }

    @Nullable
    private GraphQLObjectType getTypeForClass(Class<?> clazz, GraphQLSchema schema) {
        if (clazz.getName().startsWith("java.")) {
            return null;
        }

        String name = getMapping(clazz);
        if (name != null) {
            GraphQLObjectType objectType = schema.getObjectType(name);
            if (objectType == null) {
                throw new IllegalStateException(
                        "Invalid mapping for " + clazz.getName() + ". " +
                                "No GraphQL Object type with name '" + name + "'.");
            }
            return objectType;
        }

        name = this.classNameExtractor.apply(clazz);
        if (schema.containsType(name)) {
            return schema.getObjectType(name);
        }

        for (Class<?> interfaceType : clazz.getInterfaces()) {
            GraphQLObjectType objectType = getTypeForClass(interfaceType, schema);
            if (objectType != null) {
                return objectType;
            }
        }

        Class<?> superclass = clazz.getSuperclass();
        if (superclass != Object.class && superclass != null) {
            return getTypeForClass(superclass, schema);
        }

        return null;
    }

    @Nullable
    private String getMapping(Class<?> targetClass) {
        for (Map.Entry<Class<?>, String> entry : this.mappings.entrySet()) {
            if (entry.getKey().isAssignableFrom(targetClass)) {
                return entry.getValue();
            }
        }
        return null;
    }

}

