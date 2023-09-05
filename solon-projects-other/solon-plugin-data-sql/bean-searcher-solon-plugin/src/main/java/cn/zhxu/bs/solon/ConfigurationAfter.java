package cn.zhxu.bs.solon;

import cn.zhxu.bs.*;
import cn.zhxu.bs.FieldConvertor.BFieldConvertor;
import cn.zhxu.bs.FieldConvertor.MFieldConvertor;
import cn.zhxu.bs.convertor.*;
import cn.zhxu.bs.dialect.*;
import cn.zhxu.bs.group.*;
import cn.zhxu.bs.implement.*;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Configuration
public class ConfigurationAfter {

	@Inject
	AppContext context;

	//放到这儿，减少注入处理代码
	@Inject
	BeanSearcherProperties config;

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
		List<BFieldConvertor> convertors = context.getBeansOfType(BFieldConvertor.class);
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
		List<BFieldConvertor> convertors = context.getBeansOfType(BFieldConvertor.class);
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
		List<MFieldConvertor> convertors = context.getBeansOfType(MFieldConvertor.class);
		List<SqlInterceptor> interceptors = context.getBeansOfType(SqlInterceptor.class);
		List<ResultFilter> resultFilters = context.getBeansOfType(ResultFilter.class);
		DefaultMapSearcher searcher = new DefaultMapSearcher();
		searcher.setMetaResolver(metaResolver);
		searcher.setParamResolver(paramResolver);
		searcher.setSqlResolver(sqlResolver);
		searcher.setSqlExecutor(sqlExecutor);
		searcher.setFailOnParamError(props.getParams().isFailOnError());
		if (convertors != null) {
			List<MFieldConvertor> newList = new ArrayList<>(convertors);
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