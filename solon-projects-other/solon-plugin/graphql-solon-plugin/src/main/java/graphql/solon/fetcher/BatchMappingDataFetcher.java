package graphql.solon.fetcher;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import graphql.solon.util.ClazzUtil;
import graphql.solon.util.ReflectionUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.wrap.ParamWrap;
import org.noear.solon.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class BatchMappingDataFetcher extends SchemaMappingDataFetcher {

    private static final Object NO_VALUE = new Object();

    private final String dataLoaderKey;

    public BatchMappingDataFetcher(AppContext context, BeanWrap wrap, Method method,
            boolean isBatch, String dataLoaderKey) {
        super(context, wrap, method, isBatch);
        this.dataLoaderKey = dataLoaderKey;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        DataLoader<?, ?> dataLoader = environment.getDataLoaderRegistry()
                .getDataLoader(this.dataLoaderKey);
        if (dataLoader == null) {
            throw new IllegalStateException("No DataLoader for key '" + this.dataLoaderKey + "'");
        }
        return dataLoader.load(environment.getSource());
    }

    /**
     * Invoke the underlying batch loader method with a collection of keys to
     * return a Map of key-value pairs.
     *
     * @param keys the keys for which to load values
     * @param environment the environment available to batch loaders
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     * @return a {@code Mono} with map of key-value pairs.
     */
    @Nullable
    public <K, V> Mono<Map<K, V>> invokeForMap(Collection<K> keys,
            BatchLoaderEnvironment environment) {
        Object[] args = getMethodArgumentValues(keys, environment);
        if (doesNotHaveAsyncArgs(args)) {
            Object result = this.invokeMethod(args);
            return toMonoMap(result);
        }
        return toArgsMono(args).flatMap(argValues -> {
            Object result = this.invokeMethod(argValues);
            return toMonoMap(result);
        });
    }

    private <K> Object[] getMethodArgumentValues(Collection<K> keys,
            BatchLoaderEnvironment environment) {

        Object[] args = new Object[this.paramWraps.length];
        for (int i = 0; i < this.paramWraps.length; i++) {
            ParamWrap paramWrap = this.paramWraps[i];
            args[i] = resolve(keys, paramWrap, environment);
        }
        return args;
    }

    private <K> Object resolve(Collection<K> keys, ParamWrap paramWrap,
            BatchLoaderEnvironment environment) {
        Class<?> parameterType = paramWrap.getType();
        Parameter parameter = paramWrap.getParameter();
        String name = parameter.getName();

        if (Collection.class.isAssignableFrom(parameterType)) {
            if (parameterType.isInstance(keys)) {
                return keys;
            }

            Class<?> elementType = ClazzUtil.getGenericReturnClass(parameterType);
            Collection<K> collection = createCollection(parameterType, elementType, keys.size());
            collection.addAll(keys);
            return collection;
        } else if (parameterType.equals(GraphQLContext.class)) {
            return environment.getContext();
        } else if (parameterType.isInstance(environment)) {
            return environment;
        } else if ("kotlin.coroutines.Continuation".equals(parameterType.getName())) {
            return null;
        } else {
            throw new IllegalStateException("Could not resolve parameter: " + name);
        }
    }

    public static <E> Collection<E> createCollection(Class<?> collectionType,
            @Nullable Class<?> elementType, int capacity) {
        if (Objects.isNull(collectionType)) {
            throw new IllegalArgumentException("Collection type must not be null");
        }
        if (collectionType.isInterface()) {
            if (Set.class == collectionType || Collection.class == collectionType) {
                return new LinkedHashSet<>(capacity);
            } else if (List.class == collectionType) {
                return new ArrayList<>(capacity);
            } else if (SortedSet.class == collectionType || NavigableSet.class == collectionType) {
                return new TreeSet<>();
            } else {
                throw new IllegalArgumentException(
                        "Unsupported Collection interface: " + collectionType.getName());
            }
        } else if (EnumSet.class.isAssignableFrom(collectionType)) {
            if (Objects.isNull(elementType)) {
                throw new IllegalArgumentException(
                        "Cannot create EnumSet for unknown element type");
            }
            // Cast is necessary for compilation in Eclipse 4.4.1.
            return (Collection<E>) EnumSet.noneOf(asEnumType(elementType));
        } else {
            if (!Collection.class.isAssignableFrom(collectionType)) {
                throw new IllegalArgumentException(
                        "Unsupported Collection type: " + collectionType.getName());
            }
            try {
                return (Collection<E>) ReflectionUtils.accessibleConstructor(collectionType)
                        .newInstance();
            } catch (Throwable ex) {
                throw new IllegalArgumentException(
                        "Could not instantiate Collection type: " + collectionType.getName(), ex);
            }
        }
    }

    private static Class<? extends Enum> asEnumType(Class<?> enumType) {
        if (Objects.isNull(enumType)) {
            throw new IllegalArgumentException("Enum type must not be null");
        }
        if (!Enum.class.isAssignableFrom(enumType)) {
            throw new IllegalArgumentException(
                    "Supplied type is not an enum: " + enumType.getName());
        }
        return enumType.asSubclass(Enum.class);
    }

    /**
     * Invoke the underlying batch loader method with a collection of input keys
     * to return a collection of matching values.
     *
     * @param keys the keys for which to load values
     * @param environment the environment available to batch loaders
     * @param <V> the type of values returned
     * @return a {@code Flux} of values.
     */
    public <V> Flux<V> invokeForIterable(Collection<?> keys, BatchLoaderEnvironment environment) {
        Object[] args = getMethodArgumentValues(keys, environment);
        if (doesNotHaveAsyncArgs(args)) {
            Object result = this.invokeMethod(args);
            return toFlux(result);
        }
        return toArgsMono(args).flatMapMany(resolvedArgs -> {
            Object result = this.invokeMethod(resolvedArgs);
            return toFlux(result);
        });
    }

    private boolean doesNotHaveAsyncArgs(Object[] args) {
        return Arrays.stream(args).noneMatch(arg -> arg instanceof Mono);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Mono<Map<K, V>> toMonoMap(@Nullable Object result) {
        if (result instanceof Map) {
            return Mono.just((Map<K, V>) result);
        } else if (result instanceof Mono) {
            return (Mono<Map<K, V>>) result;
        } else if (result instanceof CompletableFuture) {
            return Mono.fromFuture((CompletableFuture<? extends Map<K, V>>) result);
        }
        return Mono.error(new IllegalStateException("Unexpected return value: " + result));
    }

    @SuppressWarnings("unchecked")
    private static <V> Flux<V> toFlux(@Nullable Object result) {
        if (result instanceof Collection) {
            return Flux.fromIterable((Collection<V>) result);
        } else if (result instanceof Flux) {
            return (Flux<V>) result;
        } else if (result instanceof CompletableFuture) {
            return Mono.fromFuture((CompletableFuture<? extends Collection<V>>) result)
                    .flatMapIterable(Function.identity());
        }
        return Flux.error(new IllegalStateException("Unexpected return value: " + result));
    }

    /**
     * Use this method to resolve the arguments asynchronously. This is only
     * useful when at least one of the values is a {@link Mono}
     */
    @SuppressWarnings("unchecked")
    protected Mono<Object[]> toArgsMono(Object[] args) {

        List<Mono<Object>> monoList = Arrays.stream(args)
                .map(arg -> {
                    Mono<Object> argMono = (arg instanceof Mono ? (Mono<Object>) arg
                            : Mono.justOrEmpty(arg));
                    return argMono.defaultIfEmpty(NO_VALUE);
                })
                .collect(Collectors.toList());

        return Mono.zip(monoList,
                values -> Stream.of(values).map(value -> value != NO_VALUE ? value : null)
                        .toArray());
    }
}
