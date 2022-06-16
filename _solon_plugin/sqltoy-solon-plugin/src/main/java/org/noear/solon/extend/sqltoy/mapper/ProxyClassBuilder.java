package org.noear.solon.extend.sqltoy.mapper;

import org.noear.liquor.DynamicCompiler;
import org.noear.solon.extend.sqltoy.annotation.Param;
import org.noear.solon.extend.sqltoy.annotation.Sql;
import org.sagacity.sqltoy.SqlToyContext;
import org.sagacity.sqltoy.config.model.SqlToyConfig;
import org.sagacity.sqltoy.config.model.SqlType;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.Page;
import org.sagacity.sqltoy.utils.StringUtil;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 编译Sql的代理类
 *
 * @author 夜の孤城
 * @since 1.5
 */
public class ProxyClassBuilder {
    //包名取在org.sagacity.sqltoy下,以便sqltoy的log输出正常定位
    private String packageName = "org.sagacity.sqltoy.solon.impl";
    private String className;
    private StringBuilder source = new StringBuilder();
    private Class type;
    private SqlToyContext context;

    public ProxyClassBuilder(SqlToyContext context, Class type) {
        this.type = type;
        String name = type.getName().replaceAll("\\.", "_") + "Impl";
        this.className = StringUtil.toHumpStr(name, true, true);
        this.context = context;
        buildSource();
    }

    private void buildSource() {
        String prefix = String.format("package %s;\n", packageName);
        source.append(prefix);
        source.append("import org.sagacity.sqltoy.model.*;\n");
        source.append("import java.util.*;\n");
        source.append("import java.util.stream.Collectors;\n");
        source.append("@SuppressWarnings(\"unchecked\")\n");
        source.append("public class " + className);
        source.append(" extends org.noear.solon.extend.sqltoy.mapper.AbstractMapper");
        source.append(" implements " + type.getName() + "{\n");

        for (Method method : type.getDeclaredMethods()) {
            if (method.isDefault()) {
                continue;
            }
            buildMethod(method);
        }

        source.append("\n}");
    }

    public String getSource() {
        return source.toString();
    }

    private void buildMethod(Method method) {
        source.append("public ");
        Class retType = method.getReturnType();
        boolean retVoid = false;
        if (Void.TYPE.equals(retType)) {
            source.append("void");
            retVoid = true;
        } else {
            source.append(method.getReturnType().getName());
        }
        source.append(" ");
        source.append(method.getName());
        source.append("(");
        boolean first = true;
        for (Parameter p : method.getParameters()) {
            if (first) {
                first = false;
            } else {
                source.append(",");
            }
            source.append(p.toString());
        }
        source.append("){\n\t");
        if(retType== SqlToyLazyDao.class){
            Class[] paramTypes = method.getParameterTypes();
            Parameter[] methodParameters = method.getParameters();
            for(int i=0;i<paramTypes.length;i++){
                Class pt=paramTypes[i];
                if(pt==String.class){
                    source.append("return _getDao("+methodParameters[i].getName()+");\n");
                    source.append("}\n");
                    return;
                }
            }
            source.append("return dao;\n");
            source.append("}\n");
            return;
        }


        Sql sql = method.getAnnotation(Sql.class);
        String sqlIdOrSql = method.getName();
        if (sql != null) {
            sqlIdOrSql = sql.value().trim();
        }
        SqlType sqlType = SqlType.search;
        int spaceIndex = sqlIdOrSql.indexOf(" ");
        //有任意空白都认为不是ID
        if (spaceIndex == -1) {
            try {
                SqlToyConfig cfg = context.getScriptLoader().getSqlConfig(sqlIdOrSql, null, "");
                if (cfg == null) {
                    throw new IllegalArgumentException("请检查 sqlId \"" + sqlIdOrSql + "\" 是否存在!");
                }
                String rawSql = cfg.getSql();
                spaceIndex = rawSql.indexOf(" ");
                sqlType = SqlType.getSqlType(rawSql.substring(0, spaceIndex));
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException("请检查 sqlId \"" + sqlIdOrSql + "\" 是否存在!");
            }
        } else {
            sqlType = SqlType.getSqlType(sqlIdOrSql.substring(0, spaceIndex));
        }

        Class[] paramTypes = method.getParameterTypes();
        Parameter[] methodParameters = method.getParameters();

        //有没有Page参数
        int _pageIdx = -1;
        //查询参数是否用Map
        int _mapParamIdx = -1;
        int _objIdx = -1;
        int _listIdx = -1;

        Map<String, Integer> namedParamIdxMap = new HashMap<>();

        boolean hasNamedparam=false;
        for (int i = 0; i < paramTypes.length; i++) {
            Param param = methodParameters[i].getAnnotation(Param.class);
            if(param!=null){
                hasNamedparam=true;
                break;
            }
            Class paramType = paramTypes[i];
            if(isPrimitive(paramType)||List.class.isAssignableFrom(paramType)||paramType.isArray()||paramType.isEnum()){
                hasNamedparam=true;
                break;
            }
        }
        for (int i = 0; i < paramTypes.length; i++) {
            Class paramType = paramTypes[i];
            if (Page.class.isAssignableFrom(paramType)) {
                _pageIdx = i;
            } else if (Map.class.isAssignableFrom(paramType)) {
                _mapParamIdx = i;
            } else if (List.class.isAssignableFrom(paramType)) {
                _listIdx = i;
            } else {
                _objIdx = i;
            }
            Param param = methodParameters[i].getAnnotation(Param.class);
            if (param != null) {
                String paramName = param.value();
                if (StringUtil.isBlank(paramName)) {
                    paramName = methodParameters[i].getName();
                }
                namedParamIdxMap.put(paramName, i);
            } else if(hasNamedparam){
                if(!Page.class.isAssignableFrom(paramType)){
                    namedParamIdxMap.put(methodParameters[i].getName(), i);
                }
            }
        }
        boolean hasNamedParams = namedParamIdxMap.size() > 0;
        if (hasNamedParams) {
            _objIdx = -1;
            _mapParamIdx = -1;
        }
        if (_objIdx > -1) {
            if (!Serializable.class.isAssignableFrom(paramTypes[_objIdx])) {
                throw new IllegalArgumentException(type.getName() + "#" + method.getName() + "中参数:" + methodParameters[_objIdx].getName() + "应为Serializable！");
            }
        }
        source.append("String _sql=\"" + sqlIdOrSql + "\";\n\t");
        switch (sqlType) {
            case search:
                //分页查询
                if (Page.class.isAssignableFrom(retType)) {
                    int pageIdx = _pageIdx;
                    int mapParamIdx = _mapParamIdx;
                    int objIdx = _objIdx;
                    Class entityType = getGenericType(method);

                    Class resultType = entityType;
                    if (entityType == null) {
                        resultType = Map.class;
                    }

                    String pageParam = pageIdx > -1 ? methodParameters[pageIdx].getName() : "new Page()";
                    //使用Object bean 查询参数
                    if (_objIdx > -1) {
                        String qeName = methodParameters[objIdx].getName();
                        source.append("QueryExecutor queryExecutor=" + qeName + "==null?new QueryExecutor(_sql):new QueryExecutor(_sql," + qeName + ");\n\t");
                        source.append("queryExecutor.resultType(" + resultType.getName() + ".class);\n\t");
                        source.append("Page _result = dao.findPageByQuery(" + pageParam + ",queryExecutor).getPageResult();\n\t");
                        source.append("return _result;\n");
                    } else {
                        String mp = mapParamIdx > -1 ? methodParameters[mapParamIdx].getName() : "new HashMap<>();\n\t";
                        source.append(" Map<String, Object> _queryMap = " + mp);
                        if (hasNamedParams) {
                            for (String k : namedParamIdxMap.keySet()) {
                                source.append("_queryMap.put(\"" + k + "\"," + k + ");\n\t");
                            }
                        }
                        source.append(" QueryExecutor queryExecutor = new QueryExecutor(_sql, _queryMap);\n\t");
                        source.append(" queryExecutor.resultType(" + entityType.getName() + ".class);\n\t");
                        source.append(" Page _result = dao.findPageByQuery(" + pageParam + ", queryExecutor).getPageResult();\n\t");
                        source.append("return _result;\n");
                    }
                } else if (List.class.isAssignableFrom(retType)) {
                    //返回列表

                    int mapParamIdx = _mapParamIdx;
                    int objIdx = _objIdx;
                    Class entityType = getGenericType(method);
                    if (entityType == null) {
                        entityType = Map.class;
                    }
                    Class resultType = entityType;

                    if (_objIdx > -1) {
                        String qeName = methodParameters[objIdx].getName();
                        source.append("QueryExecutor queryExecutor=" + qeName + "==null?new QueryExecutor(_sql):new QueryExecutor(_sql," + qeName + ");\n\t");
                        source.append("queryExecutor.resultType(" + resultType.getName() + ".class);\n\t");
                        source.append("return dao.findByQuery(queryExecutor).getRows();\n\t");
//
                    } else {
                        String mp = mapParamIdx > -1 ? methodParameters[mapParamIdx].getName() : "new HashMap<>();\n\t";
                        source.append(" Map<String, Object> _queryMap = " + mp);
                        if (hasNamedParams) {
                            for (String k : namedParamIdxMap.keySet()) {
                                source.append("_queryMap.put(\"" + k + "\"," + k + ");\n\t");
                            }
                        }
                        source.append("QueryExecutor queryExecutor = new QueryExecutor(_sql, _queryMap).resultType(" + resultType.getName() + ".class);\n\t");
                        source.append("return dao.findByQuery(queryExecutor).getRows();\n\t");
                    }
                } else {
                    //查询单值

                    if (_objIdx > -1) {
                        int objIdx = _objIdx;
                        String qeName = methodParameters[objIdx].getName();
                        source.append("QueryExecutor queryExecutor=" + qeName + "==null?new QueryExecutor(_sql):new QueryExecutor(_sql," + qeName + ");\n\t");

                        source.append("queryExecutor.resultType(" + retType.getName() + ".class);\n\t");
                        source.append(" List _result = dao.findByQuery(queryExecutor).getRows();\n\t");
                        source.append(" if (_result == null || _result.size() == 0){\n\treturn _cast(null," + retType.getName() + ".class);\n\t}");
                        source.append("return (" + retType.getName() + ")_result.get(0);\n\t");
                    } else {
                        int mapParamIdx = _mapParamIdx;
                        String mp = mapParamIdx > -1 ? methodParameters[mapParamIdx].getName() : "new HashMap<>();\n\t";
                        source.append(" Map<String, Object> _queryMap = " + mp);
                        if (hasNamedParams) {
                            for (String k : namedParamIdxMap.keySet()) {
                                source.append("_queryMap.put(\"" + k + "\"," + k + ");\n\t");
                            }
                        }
                        source.append("QueryExecutor queryExecutor = new QueryExecutor(_sql,_queryMap);\n\t");

                        source.append(" queryExecutor.resultType(" + retType.getName() + ".class);\n\t");

                        source.append("List _result = dao.findByQuery(queryExecutor).getRows();\n\t");

                        source.append(" if (_result == null || _result.size() == 0){return _cast(null," + retType.getName() + ".class);}\n\t");
                        source.append("return (" + retType.getName() + ")_result.get(0);\n\t");
                    }

                }
                break;
            case insert:
            case delete:
                if (_objIdx > -1) {
                    int objIdx = _objIdx;
                    String qeName = methodParameters[objIdx].getName();
                    source.append(convertLongReturnType("dao.executeSql(_sql," + qeName + ")", retType));
                } else {
                    int mapParamIdx = _mapParamIdx;
                    String mp = mapParamIdx > -1 ? methodParameters[mapParamIdx].getName() : "new HashMap<>();\n\t";
                    source.append(" Map<String, Object> _queryMap = " + mp);
                    if (hasNamedParams) {
                        for (String k : namedParamIdxMap.keySet()) {
                            source.append("_queryMap.put(\"" + k + "\"," + k + ");");
                        }
                    }
                    source.append(convertLongReturnType("dao.executeSql(_sql,_queryMap)", retType));
                }
                break;
            case update:
                if (_listIdx > -1 && sql.batch()) {// batch update
                    int listIdx = _listIdx;
                    String lName = methodParameters[listIdx].getName();
                    source.append(convertLongReturnType("dao.batchUpdate(_sql," + lName + ")", retType));
                } else if (_objIdx > -1) {
                    int objIdx = _objIdx;
                    String lName = methodParameters[objIdx].getName();
                    source.append(convertLongReturnType("dao.executeSql(_sql," + lName + ")", retType));
                } else {
                    int mapParamIdx = _mapParamIdx;
                    //  String mp=methodParameters[mapParamIdx].getName();
                    String mp = mapParamIdx > -1 ? methodParameters[mapParamIdx].getName() : "new HashMap<>();";
                    source.append(" Map<String, Object> _queryMap = " + mp);
                    if (hasNamedParams) {
                        for (String k : namedParamIdxMap.keySet()) {
                            source.append("_queryMap.put(\"" + k + "\"," + k + ");");
                        }
                    }
                    source.append(convertLongReturnType("dao.executeSql(_sql,_queryMap)", retType));
                }
                break;
            default:
                source.append("return null;");
        }
        source.append("}\n");
    }

    //转换返回语句执行的返回类型，只支持Long,Integer,Boolean
    private String convertLongReturnType(String ret, Class toType) {
        if (Boolean.class == toType || boolean.class == toType) {
            return "return " + ret + ">=1;\n";
        }
        if (Integer.class == toType || int.class == toType) {
            return "return " + ret + ".intValue();\n";
        }
        if (Void.class == toType || void.class == toType) {
            return ret + ";\n";
        }
        return "return " + ret + ";\n";
    }

    private void appendPagePrimitive(Class entityType) {
        source.append("List<Map> _rows = _result.getRows();\n\t");
        source.append("List _distRows = _castOneColumn(_rows," + entityType.getName() + ".class);\n\t");
        source.append("_result.setRows(_distRows);\n\t");
    }

    public Class compile() {
        //调用liquor进行动态编译
        String fullName = String.format("%s.%s", packageName, className);
        DynamicCompiler compiler = new DynamicCompiler();
        compiler.addSource(fullName, source.toString());
        Map<String, Class<?>> classMap = compiler.build();
        return classMap.get(fullName);
    }

    /**
     * 获取范型类型
     * @param method
     * @return
     */
    private static Class getGenericType(Method method) {
        Type t = method.getGenericReturnType();
        if (t instanceof ParameterizedType) {
            ParameterizedType t1 = (ParameterizedType) method.getGenericReturnType();
            return (Class) t1.getActualTypeArguments()[0];
        } else {
            return null;
        }
    }
    private static final Set<Class> primitiveTypes = new HashSet(Arrays.asList(new Class[]{
            String.class,
            Date.class,
            java.sql.Date.class,
            BigDecimal.class,
            BigInteger.class,
            Long.class,
            long.class,
            Integer.class,
            int.class,
            Byte.class,
            byte.class,
            Double.class,
            double.class,
            Float.class,
            float.class,
            Character.class,
            char.class,
            Boolean.class,
            boolean.class,
            Short.class,
            short.class
    }));

    /**
     * 是否为原生类型，包括String,Date等
     *
     * @param retType
     * @return
     */
    protected static boolean isPrimitive(Class retType) {
        if (retType == null) {
            return false;
        }
        return retType.isPrimitive() || primitiveTypes.contains(retType);
    }
}
