package graphql.solon.execution;

import graphql.language.InterfaceTypeDefinition;
import graphql.language.UnionTypeDefinition;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.CombinedWiringFactory;
import graphql.schema.idl.NoopWiringFactory;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.WiringFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import graphql.solon.configurer.RuntimeWiringConfigurer;
import graphql.solon.exception.MissingSchemaException;
import graphql.solon.resource.Resource;
import org.noear.solon.lang.Nullable;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class DefaultSchemaResourceGraphQlSourceBuilder {

    private final Set<Resource> schemaResources;

    private final List<RuntimeWiringConfigurer> runtimeWiringConfigurers;

    @Nullable
    private TypeResolver typeResolver;

    @Nullable
    private BiFunction<TypeDefinitionRegistry, RuntimeWiring, GraphQLSchema> schemaFactory;

    public DefaultSchemaResourceGraphQlSourceBuilder() {
        this.schemaResources = new LinkedHashSet<>();
        this.runtimeWiringConfigurers = new LinkedList<>();
    }

    public DefaultSchemaResourceGraphQlSourceBuilder schemaResources(
            Collection<Resource> resources) {
        this.schemaResources.addAll(resources);
        return this;
    }

    public DefaultSchemaResourceGraphQlSourceBuilder configureRuntimeWiring(
            RuntimeWiringConfigurer configurer) {
        this.runtimeWiringConfigurers.add(configurer);
        return this;
    }

    public DefaultSchemaResourceGraphQlSourceBuilder defaultTypeResolver(
            TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
        return this;
    }

    public DefaultSchemaResourceGraphQlSourceBuilder schemaFactory(
            BiFunction<TypeDefinitionRegistry, RuntimeWiring, GraphQLSchema> schemaFactory) {

        this.schemaFactory = schemaFactory;
        return this;
    }

    public GraphQLSchema getGraphQlSchema() {
        TypeDefinitionRegistry registry = this.schemaResources.stream()
                .map(this::parse)
                .reduce(TypeDefinitionRegistry::merge)
                .orElseThrow(MissingSchemaException::new);

        RuntimeWiring runtimeWiring = initRuntimeWiring();

        TypeResolver typeResolver = initTypeResolver();
        registry.types().values().forEach(def -> {
            if (def instanceof UnionTypeDefinition || def instanceof InterfaceTypeDefinition) {
                runtimeWiring.getTypeResolvers().putIfAbsent(def.getName(), typeResolver);
            }
        });

        return (this.schemaFactory != null ?
                this.schemaFactory.apply(registry, runtimeWiring) :
                new SchemaGenerator().makeExecutableSchema(registry, runtimeWiring));
    }

    private TypeDefinitionRegistry parse(Resource schemaResource) {
        if (Objects.isNull(schemaResource)) {
            throw new IllegalArgumentException("'schemaResource' not provided");
        }
        if (!schemaResource.exists()) {
            throw new IllegalArgumentException("'schemaResource' must exist: " + schemaResource);
        }
        try {
            try (InputStream inputStream = schemaResource.getInputStream()) {
                return new SchemaParser().parse(inputStream);
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to load schema resource: " + schemaResource);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to parse schema resource: " + schemaResource,
                    ex);
        }
    }

    private RuntimeWiring initRuntimeWiring() {
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();
        this.runtimeWiringConfigurers.forEach(configurer -> configurer.configure(builder));

        List<WiringFactory> factories = new ArrayList<>();
        WiringFactory factory = builder.build().getWiringFactory();
        if (!factory.getClass().equals(NoopWiringFactory.class)) {
            factories.add(factory);
        }
        this.runtimeWiringConfigurers
                .forEach(configurer -> configurer.configure(builder, factories));
        if (!factories.isEmpty()) {
            builder.wiringFactory(new CombinedWiringFactory(factories));
        }

        return builder.build();
    }

    private TypeResolver initTypeResolver() {
        return (this.typeResolver != null ? this.typeResolver : new ClassNameTypeResolver());
    }

}
