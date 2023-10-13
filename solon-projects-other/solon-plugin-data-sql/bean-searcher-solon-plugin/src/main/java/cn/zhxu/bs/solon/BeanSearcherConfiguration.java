package cn.zhxu.bs.solon;

import cn.zhxu.bs.*;
import cn.zhxu.bs.convertor.*;
import cn.zhxu.bs.dialect.*;
import cn.zhxu.bs.filter.SizeLimitParamFilter;
import cn.zhxu.bs.group.*;
import cn.zhxu.bs.implement.*;
import cn.zhxu.bs.solon.BeanSearcherProperties.Sql;
import cn.zhxu.bs.util.LRUCache;
import cn.zhxu.xjson.JsonKit;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Configuration
public class BeanSearcherConfiguration {
    @Inject
    AppContext context;

    //放到这儿，减少注入处理代码
    @Inject
    BeanSearcherProperties config;

    @Bean
    @Condition(onMissingBean = BoolParamConvertor.class)
    public BoolParamConvertor boolParamConvertor() {
        return new BoolParamConvertor();
    }

    @Bean
    @Condition(onMissingBean = NumberParamConvertor.class)
    public NumberParamConvertor numberParamConvertor() {
        return new NumberParamConvertor();
    }

    @Bean
    @Condition(onMissingBean = DateParamConvertor.class)
    public DateParamConvertor dateParamConvertor() {
        return new DateParamConvertor(config.getParams().getConvertor().getDateTarget());
    }

    @Bean
    @Condition(onMissingBean = TimeParamConvertor.class)
    public TimeParamConvertor timeParamConvertor() {
        return new TimeParamConvertor(config.getParams().getConvertor().getTimeTarget());
    }

    @Bean
    @Condition(onMissingBean = DateTimeParamConvertor.class)
    public DateTimeParamConvertor dateTimeParamConvertor() {
        return new DateTimeParamConvertor(config.getParams().getConvertor().getDateTimeTarget());
    }

    @Bean
    @Condition(onMissingBean = EnumParamConvertor.class)
    public EnumParamConvertor enumParamConvertor() {
        return new EnumParamConvertor();
    }

    @Bean
    @Condition(onMissingBean = SizeLimitParamFilter.class)
    public SizeLimitParamFilter sizeLimitParamFilter() {
        return new SizeLimitParamFilter(config.getParams().getFilter().getMaxParaMapSize());
    }

    @Bean
    @Condition(onMissingBean = PageExtractor.class)
    public PageExtractor pageExtractor() {
        BeanSearcherProperties.Params.Pagination conf = config.getParams().getPagination();
        String type = conf.getType();
        BasePageExtractor extractor;
        if (BeanSearcherProperties.Params.Pagination.TYPE_PAGE.equals(type)) {
            PageSizeExtractor p = new PageSizeExtractor();
            p.setPageName(conf.getPage());
            extractor = p;
        } else if (BeanSearcherProperties.Params.Pagination.TYPE_OFFSET.equals(type)) {
            PageOffsetExtractor p = new PageOffsetExtractor();
            p.setOffsetName(conf.getOffset());
            extractor = p;
        } else {
            throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.type: " + type + "], only 'page' / 'offset' allowed.");
        }
        int defaultSize = conf.getDefaultSize();
        int maxAllowedSize = conf.getMaxAllowedSize();
        long maxAllowedOffset = conf.getMaxAllowedOffset();
        if (defaultSize > maxAllowedSize) {
            throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.default-size: " + defaultSize + "] can not greater than [bean-searcher.params.pagination.max-allowed-size: " + maxAllowedSize + "].");
        }
        if (defaultSize < 1) {
            throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.default-size: " + defaultSize + "] must greater equal 1");
        }
        if (maxAllowedOffset < 1) {
            throw new IllegalConfigException("Invalid config: [bean-searcher.params.pagination.max-allowed-offset: " + maxAllowedOffset + "] must greater equal 1");
        }
        extractor.setMaxAllowedSize(maxAllowedSize);
        extractor.setMaxAllowedOffset(maxAllowedOffset);
        extractor.setDefaultSize(defaultSize);
        extractor.setSizeName(conf.getSize());
        extractor.setStart(conf.getStart());
        return extractor;
    }

    @Bean
    @Condition(onMissingBean = Dialect.class)
    public Dialect dialect() {
        Sql conf = config.getSql();
        Sql.Dialect defaultType = conf.getDialect();
        if (defaultType == null) {
            throw new IllegalConfigException("Invalid config: [bean-searcher.sql.dialect] can not be null.");
        }
        Dialect defaultDialect = createDialect(defaultType, "dialect");
        if (conf.isDialectDynamic()) {
            DynamicDialect dynamicDialect = new DynamicDialect();
            dynamicDialect.setDefaultDialect(defaultDialect);
            List<DataSourceDialect> dialects = context.getBeansOfType(DataSourceDialect.class);
            dialects.forEach(item -> dynamicDialect.put(item.getDataSource(), item.getDialect()));
            conf.getDialects().forEach((ds, dType) ->
                    dynamicDialect.put(ds, createDialect(dType, "dialects." + ds)));
            return dynamicDialect;
        }
        return defaultDialect;
    }

    private Dialect createDialect(Sql.Dialect dialectType, String propKey) {
        switch (dialectType) {
            case MySQL:
                return new MySqlDialect();
            case Oracle:
                return new OracleDialect();
            case PostgreSQL:
            case PgSQL:
                return new PostgreSqlDialect();
            case SqlServer:
                return new SqlServerDialect();
        }
        throw new IllegalConfigException("Invalid config: [bean-searcher.sql." + propKey + ": " + dialectType + "]. " +
                "Please see https://bs.zhxu.cn/guide/latest/advance.html#sql-%E6%96%B9%E8%A8%80%EF%BC%88dialect%EF%BC%89 for help.");
    }

    @Bean
    @Condition(onMissingBean = DynamicDialectSupport.class, onProperty = "${bean-searcher.sql.dialect-dynamic}=true")
    public DynamicDialectSupport dynamicDialectSupport() {
        return new DynamicDialectSupport();
    }

    @Bean
    @Condition(onMissingBean = ExprParser.Factory.class)
    public ExprParser.Factory parserFactory() {
        return new DefaultParserFactory();
    }

    @Bean
    @Condition(onMissingBean = GroupResolver.class)
    public GroupResolver groupResolver(ExprParser.Factory parserFactory) {
        DefaultGroupResolver groupResolver = new DefaultGroupResolver();
        BeanSearcherProperties.Params.Group conf = config.getParams().getGroup();
        groupResolver.setEnabled(conf.isEnable());
        groupResolver.setCache(new LRUCache<>(conf.getCacheSize()));
        groupResolver.setMaxExprLength(conf.getMaxExprLength());
        groupResolver.setParserFactory(parserFactory);
        return groupResolver;
    }

    @Bean
    @Condition(onMissingBean = GroupPair.Resolver.class)
    public GroupPair.Resolver groupPairResolver() {
        return new GroupPairResolver();
    }

    @Bean
    @Condition(onMissingBean = SqlResolver.class)
    public SqlResolver sqlResolver(Dialect dialect, GroupPair.Resolver groupPairResolver) {
        DefaultSqlResolver resolver = new DefaultSqlResolver(dialect);
        resolver.setGroupPairResolver(groupPairResolver);
        return resolver;
    }

    @Bean
    @Condition(onMissingBean = NumberFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-number:true}=true")
    public NumberFieldConvertor numberFieldConvertor() {
        return new NumberFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = StrNumFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-str-num:true}=true")
    public StrNumFieldConvertor strNumFieldConvertor() {
        return new StrNumFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = BoolNumFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-bool-num:true}=true")
    public BoolNumFieldConvertor boolNumFieldConvertor() {
        return new BoolNumFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = BoolFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-bool:true}=true")
    public BoolFieldConvertor boolFieldConvertor() {
        String[] falseValues = config.getFieldConvertor().getBoolFalseValues();
        BoolFieldConvertor convertor = new BoolFieldConvertor();
        if (falseValues != null) {
            convertor.addFalseValues(falseValues);
        }
        return convertor;
    }

    @Bean
    @Condition(onMissingBean = DateFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-date:true}=true")
    public DateFieldConvertor dateFieldConvertor() {
        DateFieldConvertor convertor = new DateFieldConvertor();
        ZoneId zoneId = config.getFieldConvertor().getZoneId();
        if (zoneId != null) {
            convertor.setZoneId(zoneId);
        }
        return convertor;
    }

    @Bean
    @Condition(onMissingBean = TimeFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-time:true}=true")
    public TimeFieldConvertor timeFieldConvertor() {
        return new TimeFieldConvertor();
    }

    @Bean
    @Condition(onMissingBean = EnumFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-enum:true}=true")
    public EnumFieldConvertor enumFieldConvertor() {
        BeanSearcherProperties.FieldConvertor conf = config.getFieldConvertor();
        EnumFieldConvertor convertor = new EnumFieldConvertor();
        convertor.setFailOnError(conf.isEnumFailOnError());
        convertor.setIgnoreCase(conf.isEnumIgnoreCase());
        return convertor;
    }

    //
    // 在 springboot 那边，是用单独类处理的；在 solon 这边，用函数
    //
    @Bean
    @Condition(onMissingBean = JsonFieldConvertor.class, onClass = JsonKit.class,
            onProperty = "${bean-searcher.field-convertor.use-json:true}=true")
    public JsonFieldConvertor jsonFieldConvertor() {
        BeanSearcherProperties.FieldConvertor conf = config.getFieldConvertor();
        return new JsonFieldConvertor(conf.isJsonFailOnError());
    }

    @Bean
    @Condition(onMissingBean = ListFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-list:true}=true")
    @SuppressWarnings("all")
    public ListFieldConvertor listFieldConvertor() {
        List<ListFieldConvertor.Convertor> tmp = context.getBeansOfType(ListFieldConvertor.Convertor.class);
        List<ListFieldConvertor.Convertor<?>> convertors = new ArrayList<>();
        tmp.forEach(convertors::add);
        BeanSearcherProperties.FieldConvertor conf = config.getFieldConvertor();
        ListFieldConvertor convertor = new ListFieldConvertor(conf.getListItemSeparator());
        if (convertors != null) {
            convertor.setConvertors(convertors);
        }
        return convertor;
    }

    @Bean
    @Condition(onMissingBean = DbMapping.class)
    public DbMapping dbMapping() {
        DefaultDbMapping mapping = new DefaultDbMapping();
        Sql.DefaultMapping conf = config.getSql().getDefaultMapping();
        mapping.setTablePrefix(conf.getTablePrefix());
        mapping.setUpperCase(conf.isUpperCase());
        mapping.setUnderlineCase(conf.isUnderlineCase());
        mapping.setRedundantSuffixes(conf.getRedundantSuffixes());
        mapping.setIgnoreFields(conf.getIgnoreFields());
        mapping.setDefaultInheritType(conf.getInheritType());
        mapping.setDefaultSortType(conf.getSortType());
        mapping.setAroundChar(conf.getAroundChar());
        return mapping;
    }

    @Bean
    @Condition(onMissingBean = MetaResolver.class)
    public MetaResolver metaResolver(DbMapping dbMapping) {
        SnippetResolver snippetResolver = context.getBean(SnippetResolver.class);
        DefaultMetaResolver metaResolver = new DefaultMetaResolver(dbMapping);
        if (snippetResolver != null) {
            metaResolver.setSnippetResolver(snippetResolver);
        }
        return metaResolver;
    }

    @Bean
    @Condition(onMissingBean = DateFormatFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-date-format:true}=true")
    public DateFormatFieldConvertor dateFormatFieldConvertor() {
        BeanSearcherProperties.FieldConvertor conf = config.getFieldConvertor();
        Map<String, String> dateFormats = conf.getDateFormats();
        ZoneId zoneId = conf.getZoneId();
        DateFormatFieldConvertor convertor = new DateFormatFieldConvertor();
        if (dateFormats != null) {
            dateFormats.forEach((key, value) -> {
                // 由于在 yml 的 key 中的 `:` 会被自动过滤，所以这里做下特殊处理，在 yml 中可以用 `-` 替代
                String scope = key.replace('-', ':');
                convertor.setFormat(scope, value);
            });
        }
        if (zoneId != null) {
            convertor.setZoneId(zoneId);
        }
        return convertor;
    }

    @Bean
    @Condition(onMissingBean = FieldOpPool.class)
    public FieldOpPool fieldOpPool(Dialect dialect) {
        List<FieldOp> fieldOps = context.getBeansOfType(FieldOp.class);
        FieldOpPool pool = new FieldOpPool();
        ifAvailable(fieldOps, ops -> ops.forEach(pool::addFieldOp));
        pool.setDialect(dialect);
        return pool;
    }

    @Bean
    @Condition(onMissingBean = ParamResolver.class)
    public ParamResolver paramResolver(PageExtractor pageExtractor,
                                       FieldOpPool fieldOpPool,
                                       GroupResolver groupResolver) {
        List<ParamFilter> paramFilters = context.getBeansOfType(ParamFilter.class);
        List<FieldConvertor.ParamConvertor> convertors = context.getBeansOfType(FieldConvertor.ParamConvertor.class);

        DefaultParamResolver paramResolver = new DefaultParamResolver(convertors, paramFilters);
        paramResolver.setPageExtractor(pageExtractor);
        paramResolver.setFieldOpPool(fieldOpPool);
        BeanSearcherProperties.Params conf = config.getParams();
        paramResolver.setOperatorSuffix(conf.getOperatorKey());
        paramResolver.setIgnoreCaseSuffix(conf.getIgnoreCaseKey());
        paramResolver.setOrderName(conf.getOrder());
        paramResolver.setSortName(conf.getSort());
        paramResolver.setOrderByName(conf.getOrderBy());
        paramResolver.setSeparator(conf.getSeparator());
        paramResolver.setOnlySelectName(conf.getOnlySelect());
        paramResolver.setSelectExcludeName(conf.getSelectExclude());
        BeanSearcherProperties.Params.Group group = conf.getGroup();
        paramResolver.setGexprName(group.getExprName());
        paramResolver.setGroupSeparator(group.getSeparator());
        paramResolver.setGroupResolver(groupResolver);
        return paramResolver;
    }

    @Bean
    @Condition(onMissingBean = SqlExecutor.class)
    public SqlExecutor sqlExecutor() {
        DataSource dataSource = context.getBean(DataSource.class);
        List<NamedDataSource> namedDataSources = context.getBeansOfType(NamedDataSource.class);
        SqlExecutor.SlowListener slowListener = context.getBean(SqlExecutor.SlowListener.class);

        DefaultSqlExecutor executor = new DefaultSqlExecutor(dataSource);
        ifAvailable(namedDataSources, ndsList -> {
            for (NamedDataSource nds : ndsList) {
                executor.setDataSource(nds.getName(), nds.getDataSource());
            }
        });
        ifAvailable(slowListener, executor::setSlowListener);
        executor.setSlowSqlThreshold(config.getSql().getSlowSqlThreshold());
        return executor;
    }

    @Bean
    @Condition(onMissingBean = BeanReflector.class)
    public BeanReflector beanReflector() {
        List<FieldConvertor.BFieldConvertor> convertors = context.getBeansOfType(FieldConvertor.BFieldConvertor.class);
        if (convertors != null) {
            return new DefaultBeanReflector(convertors);
        }
        return new DefaultBeanReflector();
    }

    @Bean
    @Condition(onMissingBean = BeanSearcher.class,
            onProperty = "${bean-searcher.use-bean-searcher:true}=true")
    public BeanSearcher beanSearcher(MetaResolver metaResolver,
                                     ParamResolver paramResolver,
                                     SqlResolver sqlResolver,
                                     SqlExecutor sqlExecutor,
                                     BeanReflector beanReflector,
                                     BeanSearcherProperties props) {
        List<SqlInterceptor> interceptors = context.getBeansOfType(SqlInterceptor.class);
        List<ResultFilter> processors = context.getBeansOfType(ResultFilter.class);
        DefaultBeanSearcher searcher = new DefaultBeanSearcher();
        searcher.setMetaResolver(metaResolver);
        searcher.setParamResolver(paramResolver);
        searcher.setSqlResolver(sqlResolver);
        searcher.setSqlExecutor(sqlExecutor);
        searcher.setBeanReflector(beanReflector);
        searcher.setFailOnParamError(props.getParams().isFailOnError());
        ifAvailable(interceptors, searcher::setInterceptors);
        ifAvailable(processors, searcher::setResultFilters);
        return searcher;
    }

    @Bean
    @Condition(onMissingBean = B2MFieldConvertor.class,
            onProperty = "${bean-searcher.field-convertor.use-b2-m}=true")
    public B2MFieldConvertor b2mFieldConvertor() {
        List<FieldConvertor.BFieldConvertor> convertors = context.getBeansOfType(FieldConvertor.BFieldConvertor.class);
        if (convertors != null) {
            return new B2MFieldConvertor(convertors);
        }
        return new B2MFieldConvertor(Collections.emptyList());
    }

    @Bean //@Primary
    @Condition(onMissingBean = MapSearcher.class,
            onProperty = "${bean-searcher.use-map-searcher:true}=true")
    public MapSearcher mapSearcher(MetaResolver metaResolver,
                                   ParamResolver paramResolver,
                                   SqlResolver sqlResolver,
                                   SqlExecutor sqlExecutor,
                                   BeanSearcherProperties props) {
        List<FieldConvertor.MFieldConvertor> convertors = context.getBeansOfType(FieldConvertor.MFieldConvertor.class);
        List<SqlInterceptor> interceptors = context.getBeansOfType(SqlInterceptor.class);
        List<ResultFilter> resultFilters = context.getBeansOfType(ResultFilter.class);
        DefaultMapSearcher searcher = new DefaultMapSearcher();
        searcher.setMetaResolver(metaResolver);
        searcher.setParamResolver(paramResolver);
        searcher.setSqlResolver(sqlResolver);
        searcher.setSqlExecutor(sqlExecutor);
        searcher.setFailOnParamError(props.getParams().isFailOnError());
        if (convertors != null) {
            List<FieldConvertor.MFieldConvertor> newList = new ArrayList<>(convertors);
            // 让 DateFormatFieldConvertor 排在前面
            newList.sort((o1, o2) -> {
                if (o1 instanceof DateFormatFieldConvertor) {
                    return -1;
                }
                if (o2 instanceof DateFormatFieldConvertor) {
                    return 1;
                }
                return 0;
            });
            searcher.setConvertors(newList);
        }
        ifAvailable(interceptors, searcher::setInterceptors);
        ifAvailable(resultFilters, searcher::setResultFilters);
        return searcher;
    }

    private <T> void ifAvailable(T provider, Consumer<T> consumer) {
        if (provider != null) {
            consumer.accept(provider);
        }
    }

}
