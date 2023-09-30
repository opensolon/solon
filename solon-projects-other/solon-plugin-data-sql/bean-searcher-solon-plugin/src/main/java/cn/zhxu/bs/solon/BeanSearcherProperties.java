package cn.zhxu.bs.solon;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.FieldConvertor.BFieldConvertor;
import cn.zhxu.bs.MapSearcher;
import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.InheritType;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.bean.SortType;
import cn.zhxu.bs.convertor.*;
import cn.zhxu.bs.filter.SizeLimitParamFilter;
import cn.zhxu.bs.group.DefaultGroupResolver;
import cn.zhxu.bs.implement.DefaultParamResolver;
import cn.zhxu.bs.util.MapBuilder;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Inject("${bean-searcher}")
@Configuration
public class BeanSearcherProperties {

	/**
	 * 检索参数相关配置
	 */
	private final Params params = new Params();

	/**
	 * SQL 相关配置
	 */
	private final Sql sql = new Sql();

	/**
	 * 字段转换器相关配置
	 */
	private final FieldConvertor fieldConvertor = new FieldConvertor();

	/**
	 * 是否使用 {@link MapSearcher } 检索器，默认为 true
	 */
	private boolean useMapSearcher = true;

	/**
	 * 是否使用 {@link BeanSearcher } 检索器，默认为 true
	 */
	private boolean useBeanSearcher = true;


	public Params getParams() {
		return params;
	}

	public Sql getSql() {
		return sql;
	}

	public FieldConvertor getFieldConvertor() {
		return fieldConvertor;
	}

	public static class Params {

		/**
		 * 排序字段参数名，默认为 `sort`，
		 * @see DefaultParamResolver#setSortName(String)
		 */
		private String sort = "sort";

		/**
		 * 排序方法参数名，默认为 `order`，
		 * @see DefaultParamResolver#setOrderName(String)
		 */
		private String order = "order";

		/**
		 * 排序参数名，默认为 `orderBy`，
		 * @see DefaultParamResolver#setOrderByName(String)
 		 */
		private String orderBy = "orderBy";

		/**
		 * 字段参数名分隔符，默认为 `-`，
		 * @see DefaultParamResolver#setSeparator(String)
		 */
		private String separator = "-";

		/**
		 * 是否忽略大小写字段参数名的后缀，默认为 `ic`，
		 * @see DefaultParamResolver#setIgnoreCaseSuffix(String)
		 */
		private String ignoreCaseKey = "ic";

		/**
		 * 检索运算符参数名后缀，默认为 `op`，
		 * @see DefaultParamResolver#setOperatorSuffix(String)
		 */
		private String operatorKey = "op";

		/**
		 * 指定只 Select 某些字段的参数名，默认为 `onlySelect`，
		 * @see DefaultParamResolver#setOnlySelectName(String)
		 */
		private String onlySelect = "onlySelect";

		/**
		 * 指定 Select 排除某些字段的参数名，默认为 `selectExclude`，
		 * @see DefaultParamResolver#setSelectExcludeName(String)
		 */
		private String selectExclude = "selectExclude";

		/**
		 * 参数非法时是否抛出异常
		 * @since v4.2.3
		 */
		private boolean failOnError = false;

		/**
		 * 过滤器配置
		 */
		private final Filter filter = new Filter();

		public static class Filter {

			/**
			 * 检索参数的最大尺寸，用于风险控制，避免前端恶意传参生成过于复杂的 SQL
			 * @see SizeLimitParamFilter#setMaxParaMapSize(int)
			 */
			private int maxParaMapSize = 150;

			public int getMaxParaMapSize() {
				return maxParaMapSize;
			}

			public void setMaxParaMapSize(int maxParaMapSize) {
				this.maxParaMapSize = maxParaMapSize;
			}
		}

		/**
		 * 参数组相关配置
		 */
		private final Group group = new Group();

		public static class Group {

			/**
			 * 是否启用参数组功能，默认为 true
			 */
			private boolean enable = true;

			/**
			 * 组表达式参数名，默认为 `gexpr`，
			 * @see DefaultParamResolver#setGexprName(String)
			 */
			private String exprName = "gexpr";

			/**
			 * 组参数分隔符，默认为 `.`，
			 * @see DefaultParamResolver#setGroupSeparator(String)
			 */
			private String separator = ".";

			/**
			 * 组表达式缓存大小，默认为 50
			 */
			private int cacheSize = 50;

			/**
			 * 表达式最大允许长度，风险控制，用于避免前端恶意传参生成过于复杂的 SQL
			 * @see DefaultGroupResolver#setMaxExprLength(int)
			 */
			private int maxExprLength = 50;

			public boolean isEnable() {
				return enable;
			}

			public void setEnable(boolean enable) {
				this.enable = enable;
			}

			public String getExprName() {
				return exprName;
			}

			public void setExprName(String exprName) {
				this.exprName = exprName;
			}

			public String getSeparator() {
				return separator;
			}

			public void setSeparator(String separator) {
				this.separator = separator;
			}

			public int getCacheSize() {
				return cacheSize;
			}

			public void setCacheSize(int cacheSize) {
				this.cacheSize = cacheSize;
			}

			public int getMaxExprLength() {
				return maxExprLength;
			}

			public void setMaxExprLength(int maxExprLength) {
				this.maxExprLength = maxExprLength;
			}

		}

		private final Convertor convertor = new Convertor();

		public static class Convertor {

			private DateParamConvertor.Target dateTarget = DateParamConvertor.Target.SQL_DATE;
			private DateTimeParamConvertor.Target dateTimeTarget = DateTimeParamConvertor.Target.SQL_TIMESTAMP;
			private TimeParamConvertor.Target timeTarget = TimeParamConvertor.Target.SQL_TIME;

			public DateParamConvertor.Target getDateTarget() {
				return dateTarget;
			}

			public void setDateTarget(DateParamConvertor.Target dateTarget) {
				this.dateTarget = dateTarget;
			}

			public DateTimeParamConvertor.Target getDateTimeTarget() {
				return dateTimeTarget;
			}

			public void setDateTimeTarget(DateTimeParamConvertor.Target dateTimeTarget) {
				this.dateTimeTarget = dateTimeTarget;
			}

			public TimeParamConvertor.Target getTimeTarget() {
				return timeTarget;
			}

			public void setTimeTarget(TimeParamConvertor.Target timeTarget) {
				this.timeTarget = timeTarget;
			}

		}

		/**
		 * 分页相关配置
		 */
		private final Pagination pagination = new Pagination();

		public String getSort() {
			return sort;
		}

		public void setSort(String sort) {
			this.sort = sort;
		}

		public String getOrder() {
			return order;
		}

		public void setOrder(String order) {
			this.order = order;
		}

		public String getOrderBy() {
			return orderBy;
		}

		public void setOrderBy(String orderBy) {
			this.orderBy = orderBy;
		}

		public String getSeparator() {
			return separator;
		}

		public void setSeparator(String separator) {
			this.separator = separator;
		}

		public String getIgnoreCaseKey() {
			return ignoreCaseKey;
		}

		public void setIgnoreCaseKey(String ignoreCaseKey) {
			this.ignoreCaseKey = ignoreCaseKey;
		}

		public String getOperatorKey() {
			return operatorKey;
		}

		public void setOperatorKey(String operatorKey) {
			this.operatorKey = operatorKey;
		}

		public String getOnlySelect() {
			return onlySelect;
		}

		public void setOnlySelect(String onlySelect) {
			this.onlySelect = onlySelect;
		}

		public String getSelectExclude() {
			return selectExclude;
		}

		public void setSelectExclude(String selectExclude) {
			this.selectExclude = selectExclude;
		}

		public Group getGroup() {
			return group;
		}

		public Convertor getConvertor() {
			return convertor;
		}

		public boolean isFailOnError() {
			return failOnError;
		}

		public void setFailOnError(boolean failOnError) {
			this.failOnError = failOnError;
		}

		public Pagination getPagination() {
			return pagination;
		}

		public static class Pagination {

			public static final String TYPE_PAGE = "page";

			public static final String TYPE_OFFSET = "offset";

			/**
			 * 默认分页大小，默认为 15
			 */
			private int defaultSize = 15;

			/**
			 * 分页类型，可选：`page` 和 `offset`，默认为 `page`
			 * */
			private String type = TYPE_PAGE;

			/**
			 * 分页大小参数名，默认为 `size`
			 */
			private String size = "size";

			/**
			 * 页码参数名（仅在 type = `page` 时有效），默认为 `page`
			 */
			private String page = "page";

			/**
			 * 页偏移参数名（仅在 type = `offset` 时有效），默认为 `offset`
			 */
			private String offset = "offset";

			/**
			 * 起始页码 或 起始页偏移，默认为 0，
			 * 注意：该配置对方法 {@link MapBuilder#page(long, int)} } 与 {@link MapBuilder#limit(long, int)} 无效
			 */
			private int start = 0;

			/**
			 * 分页保护：每页最大允许查询条数，默认为 100，
			 * 注意：该配置对 {@link BeanSearcher#searchAll(Class)} 与 {@link MapSearcher#searchAll(Class)} 方法无效
			 */
			private int maxAllowedSize = 100;

			/**
			 * 分页保护：最大允许偏移量，如果是 page 分页，则最大允许页码是 maxAllowedOffset / size
			 */
			private long maxAllowedOffset = 20000;

			public int getDefaultSize() {
				return defaultSize;
			}

			public void setDefaultSize(int defaultSize) {
				this.defaultSize = defaultSize;
			}

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

			public String getSize() {
				return size;
			}

			public void setSize(String size) {
				this.size = size;
			}

			public String getPage() {
				return page;
			}

			public void setPage(String page) {
				this.page = page;
			}

			public String getOffset() {
				return offset;
			}

			public void setOffset(String offset) {
				this.offset = offset;
			}

			public int getStart() {
				return start;
			}

			public void setStart(int start) {
				this.start = start;
			}

			public int getMaxAllowedSize() {
				return maxAllowedSize;
			}

			public void setMaxAllowedSize(int maxAllowedSize) {
				this.maxAllowedSize = maxAllowedSize;
			}

			public long getMaxAllowedOffset() {
				return maxAllowedOffset;
			}

			public void setMaxAllowedOffset(long maxAllowedOffset) {
				this.maxAllowedOffset = maxAllowedOffset;
			}

		}

		public Filter getFilter() {
			return filter;
		}

	}

	public static class Sql {

		enum Dialect {
			MySQL,
			Oracle,
			PostgreSQL,
			/**
			 * alias for PostgreSQL
			 */
			PgSQL,
			SqlServer
		}

		/**
		 * 数据库方言，可选：MySQL、Oracle、PostgreSql，默认为 MySQL，另可通过声明 Spring Bean 来使用其它自定义方言
		 */
		private Dialect dialect = Dialect.MySQL;

		/**
		 * 是否启用动态方言
		 */
		private boolean dialectDynamic;

		/**
		 * 多方言配置：数据源名称 -> 方言类型
		 */
		private Map<String, Dialect> dialects = new HashMap<>();

		/**
		 * 默认映射配置
		 */
		private final DefaultMapping defaultMapping = new DefaultMapping();

		/**
		 * 慢 SQL 阈值（单位：毫秒），默认：500 毫秒
		 * @since v3.7.0
		 */
		private long slowSqlThreshold = 500;

		public Dialect getDialect() {
			return dialect;
		}

		public void setDialect(Dialect dialect) {
			this.dialect = dialect;
		}

		public boolean isDialectDynamic() {
			return dialectDynamic;
		}

		public void setDialectDynamic(boolean dialectDynamic) {
			this.dialectDynamic = dialectDynamic;
		}

		public Map<String, Dialect> getDialects() {
			return dialects;
		}

		public void setDialects(Map<String, Dialect> dialects) {
			this.dialects = dialects;
		}

		public DefaultMapping getDefaultMapping() {
			return defaultMapping;
		}

		public static class DefaultMapping {

			/**
			 * 是否启动大写映射，启用后，自动映射出的表名与列名都是大写形式，默认为 false，
			 * 注意：使用 {@link SearchBean#tables() } 与 {@link DbField#value() } 显示指定的表名与列表仍保持原有大小写形式
			 */
			private boolean upperCase = false;

			/**
			 * 驼峰是否转下划线，启用后，自动映射出的表名与列名都是下划线风格，默认为 true，
			 * 注意：使用 {@link SearchBean#tables() } 与 {@link DbField#value() } 显示指定的表名与列表仍保持原有大小写形式
			 */
			private boolean underlineCase = true;

			/**
			 * 表名前缀，在自动映射表名时使用（即：当实体类没有用 {@link SearchBean#tables() } 指定表名时，框架会用该前缀与实体类名称自动生成一个表名），无默认值
			 */
			private String tablePrefix = null;

			/**
			 * 实体类的冗余后缀，在自动映射表名时使用，即：当框架用实体类名称自动生成一个表名时，会自动忽略实体类的后缀，如 VO，DTO 等，无默认值
			 */
			private String[] redundantSuffixes;

			/**
			 * 需要全局忽略的实体类属性名列表，无默认值，注意：如果属性添加的 {@link DbField } 注解，则不受该配置影响
			 */
			private String[] ignoreFields;

			/**
			 * 全局实体类继承机制，可选：`NONE`、`TABLE`、`FIELD`、`ALL`，默认为 `ALL`，注意：该配置的优先级比 {@link SearchBean#inheritType()} 低
			 */
			private InheritType inheritType = InheritType.ALL;

			/**
			 * 全局排序策略，可选：`ONLY_ENTITY`、`ALLOW_PARAM`，默认为 `ALLOW_PARAM`，注意：该配置的优先级比 {@link SearchBean#sortType()} 低
			 */
			private SortType sortType = SortType.ALLOW_PARAM;

			/**
			 * 标识符的 围绕符，以区分系统保留字，只对自动映射的表名与字段起作用（since v4.0.0）
			 */
			private String aroundChar;

			public boolean isUpperCase() {
				return upperCase;
			}

			public void setUpperCase(boolean upperCase) {
				this.upperCase = upperCase;
			}

			public boolean isUnderlineCase() {
				return underlineCase;
			}

			public void setUnderlineCase(boolean underlineCase) {
				this.underlineCase = underlineCase;
			}

			public String getTablePrefix() {
				return tablePrefix;
			}

			public void setTablePrefix(String tablePrefix) {
				this.tablePrefix = tablePrefix;
			}

			public String[] getRedundantSuffixes() {
				return redundantSuffixes;
			}

			public void setRedundantSuffixes(String[] redundantSuffixes) {
				this.redundantSuffixes = redundantSuffixes;
			}

			public String[] getIgnoreFields() {
				return ignoreFields;
			}

			public void setIgnoreFields(String[] ignoreFields) {
				this.ignoreFields = ignoreFields;
			}

			public InheritType getInheritType() {
				return inheritType;
			}

			public void setInheritType(InheritType inheritType) {
				this.inheritType = inheritType;
			}

			public SortType getSortType() {
				return sortType;
			}

			public void setSortType(SortType sortType) {
				this.sortType = sortType;
			}

			public String getAroundChar() {
				return aroundChar;
			}

			public void setAroundChar(String aroundChar) {
				this.aroundChar = aroundChar;
			}

		}

		public long getSlowSqlThreshold() {
			return slowSqlThreshold;
		}

		public void setSlowSqlThreshold(long slowSqlThreshold) {
			this.slowSqlThreshold = slowSqlThreshold;
		}

	}

	public static class FieldConvertor {

		/**
		 * 是否启用 {@link NumberFieldConvertor }，默认为 true
		 */
		private boolean useNumber = true;

		/**
		 * 是否启用 {@link StrNumFieldConvertor }，默认为 true
		 */
		private boolean useStrNum = true;

		/**
		 * 是否启用 {@link BoolNumFieldConvertor }，默认为 true
		 */
		private boolean useBoolNum = true;

		/**
		 * 是否启用 {@link BoolFieldConvertor }，默认为 true
		 */
		private boolean useBool = true;

		/**
		 * 可转换为 false 的值，可配多个，默认为：`0,OFF,FALSE,N,NO,F`，将作为 {@link BoolFieldConvertor } 的参数，
		 * @see BoolFieldConvertor#setFalseValues(String[])
		 */
		private String[] boolFalseValues;

		/**
		 * 是否启用 {@link DateFieldConvertor }，默认为 true
		 */
		private boolean useDate = true;

		/**
		 * 是否启用 {@link DateFormatFieldConvertor }，启用后，它会把 {@link MapSearcher } 检索结果中的日期字段格式化为指定格式的字符串，默认为 true，
		 * 注意：并不是所有实体类中的日期字段都会被转换，它只转换 {@link #dateFormats } 指定的范围内的实体类与字段
		 */
		private boolean useDateFormat = true;

		/**
		 * 是否启用 {@link TimeFieldConvertor }，默认为 true
		 */
		private boolean useTime = true;

		/**
		 * 时区 ID，将作为 {@link DateFieldConvertor } 与 {@link DateFormatFieldConvertor } 的参数，默认取值：{@link ZoneId#systemDefault() }，
		 * @see DateFieldConvertor#setZoneId(ZoneId)
		 * @see DateFormatFieldConvertor#setZoneId(ZoneId)
		 */
		private ZoneId zoneId = null;

		/**
		 * 日期/时间格式，{@link Map} 形式，键为 scope（生效范围，可以是 全类名.字段名、全类名:字段类型名、包名:字段类型名 或 包名，范围越小，使用优先级越高）, 值为 format（日期格式），
		 * 它将作为 {@link DateFormatFieldConvertor } 的参数
		 * @see DateFormatFieldConvertor#setFormat(String, String)
		 */
		private Map<String, String> dateFormats = new HashMap<>();

		/**
		 * 是否启用 {@link EnumFieldConvertor }，默认为 true
		 */
		private boolean useEnum = true;

		/**
		 * 当数据库值不能转换为对应的枚举时，是否抛出异常
		 * @see EnumFieldConvertor#setFailOnError(boolean)
		 * @since v3.7.0
		 */
		private boolean enumFailOnError = true;

		/**
		 * 当数据库值为字符串，匹配枚举时是否忽略大小写
		 * @see EnumFieldConvertor#setIgnoreCase(boolean)
		 * @since v3.7.0
		 */
		private boolean enumIgnoreCase = false;

		/**
		 * 是否启用 {@link B2MFieldConvertor }，默认为 false。
		 * 未启用时，{@link MapSearcher } 检索结果的字段值 未经过 {@link BFieldConvertor } 的转换，所以字段类型都是原始类，可能与实体类声明的类型不一致；
		 * 启用后，将与 {@link BeanSearcher } 一样，检索结果的值类型 将被转换为 实体类中声明的类型。
		 * 注意，当 {@link #useDateFormat } 为 true 时，日期时间类型的字段可能仍会被 {@link DateFormatFieldConvertor } 格式化为字符串。
		 */
		private boolean useB2M = false;

		/**
		 * 是否启用 {@link JsonFieldConvertor }（必要条件），默认为 true，但需要注意的是，即使该参数为 true, 也不一定能成功启用 {@link JsonFieldConvertor }，
		 * 您必须还得添加 <a href="https://gitee.com/troyzhxu/xjsonkit">xjsonkit</a> 的 json 相关实现的依赖才可以，目前这些依赖有（你可以任选其一）：
		 * <pre>
		 * implementation 'cn.zhxu:xjsonkit-fastjson:1.4.2'		// Fastjson 实现
		 * implementation 'cn.zhxu:xjsonkit-fastjson2:1.4.2'	// Fastjson2 实现
		 * implementation 'cn.zhxu:xjsonkit-gson:1.4.2'			// Gson 实现
		 * implementation 'cn.zhxu:xjsonkit-jackson:1.4.2'		// Jackson 实现
		 * implementation 'cn.zhxu:xjsonkit-snack3:1.4.2'		// Snack3 实现
		 * </pre>
		 * @since v4.0.0
		 */
		private boolean useJson = true;

		/**
		 * 使用 {@link JsonFieldConvertor } 时，当遇到某些值 JSON 解析异常时，是否抛出异常
		 * @see JsonFieldConvertor#setFailOnError(boolean)
		 * @since v4.0.1
		 */
		private boolean jsonFailOnError = true;

		/**
		 * 是否启用 {@link ListFieldConvertor }，默认为 true
		 * @since v4.0.0
		 */
		private boolean useList = true;

		/**
		 * @see ListFieldConvertor#setItemSeparator(String)
		 * @since v4.0.0
		 */
		private String listItemSeparator = ",";

		public boolean isUseNumber() {
			return useNumber;
		}

		public void setUseNumber(boolean useNumber) {
			this.useNumber = useNumber;
		}

		public boolean isUseStrNum() {
			return useStrNum;
		}

		public void setUseStrNum(boolean useStrNum) {
			this.useStrNum = useStrNum;
		}

		public boolean isUseBoolNum() {
			return useBoolNum;
		}

		public void setUseBoolNum(boolean useBoolNum) {
			this.useBoolNum = useBoolNum;
		}

		public boolean isUseBool() {
			return useBool;
		}

		public void setUseBool(boolean useBool) {
			this.useBool = useBool;
		}

		public String[] getBoolFalseValues() {
			return boolFalseValues;
		}

		public void setBoolFalseValues(String[] boolFalseValues) {
			this.boolFalseValues = boolFalseValues;
		}

		public boolean isUseDate() {
			return useDate;
		}

		public void setUseDate(boolean useDate) {
			this.useDate = useDate;
		}

		public boolean isUseDateFormat() {
			return useDateFormat;
		}

		public void setUseDateFormat(boolean useDateFormat) {
			this.useDateFormat = useDateFormat;
		}

		public Map<String, String> getDateFormats() {
			return dateFormats;
		}

		public void setDateFormats(Map<String, String> dateFormats) {
			this.dateFormats = dateFormats;
		}

		public boolean isUseTime() {
			return useTime;
		}

		public void setUseTime(boolean useTime) {
			this.useTime = useTime;
		}

		public ZoneId getZoneId() {
			return zoneId;
		}

		public void setZoneId(ZoneId zoneId) {
			this.zoneId = zoneId;
		}

		public boolean isUseEnum() {
			return useEnum;
		}

		public void setUseEnum(boolean useEnum) {
			this.useEnum = useEnum;
		}

		public boolean isEnumFailOnError() {
			return enumFailOnError;
		}

		public void setEnumFailOnError(boolean enumFailOnError) {
			this.enumFailOnError = enumFailOnError;
		}

		public boolean isEnumIgnoreCase() {
			return enumIgnoreCase;
		}

		public void setEnumIgnoreCase(boolean enumIgnoreCase) {
			this.enumIgnoreCase = enumIgnoreCase;
		}

		public boolean isUseB2M() {
			return useB2M;
		}

		public void setUseB2M(boolean useB2M) {
			this.useB2M = useB2M;
		}

		public boolean isUseJson() {
			return useJson;
		}

		public void setUseJson(boolean useJson) {
			this.useJson = useJson;
		}

		public boolean isJsonFailOnError() {
			return jsonFailOnError;
		}

		public void setJsonFailOnError(boolean jsonFailOnError) {
			this.jsonFailOnError = jsonFailOnError;
		}

		public boolean isUseList() {
			return useList;
		}

		public void setUseList(boolean useList) {
			this.useList = useList;
		}

		public String getListItemSeparator() {
			return listItemSeparator;
		}

		public void setListItemSeparator(String listItemSeparator) {
			this.listItemSeparator = listItemSeparator;
		}

	}

	public boolean isUseMapSearcher() {
		return useMapSearcher;
	}

	public void setUseMapSearcher(boolean useMapSearcher) {
		this.useMapSearcher = useMapSearcher;
	}

	public boolean isUseBeanSearcher() {
		return useBeanSearcher;
	}

	public void setUseBeanSearcher(boolean useBeanSearcher) {
		this.useBeanSearcher = useBeanSearcher;
	}

}
