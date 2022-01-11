package org.noear.solon.extend.sqltoy.mapper;

import com.itranswarp.compiler.JavaStringCompiler;
import org.noear.solon.extend.sqltoy.annotation.Param;
import org.noear.solon.extend.sqltoy.annotation.Sql;
import org.sagacity.sqltoy.SqlToyContext;
import org.sagacity.sqltoy.config.model.SqlToyConfig;
import org.sagacity.sqltoy.config.model.SqlType;
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
 * @author 夜の孤城
 * @since 1.5
 * */
public class ProxyClassBuilder {
    //包名取在org.sagacity.sqltoy下,以便sqltoy的log输出正常定位
    private String packageName="org.sagacity.sqltoy.solon.impl";
    private String className;
    private StringBuilder source=new StringBuilder();
    private Class type;
    private SqlToyContext context;
    public ProxyClassBuilder(SqlToyContext context,Class type){
        this.type=type;
        String name=type.getName().replaceAll("\\.","_")+"Impl";
        this.className=StringUtil.toHumpStr(name,true,true);
        this.context=context;
        buildSource();
    }
    private void buildSource(){
        String prefix = String.format("package %s;", packageName);
        source.append(prefix);
        source.append("import org.sagacity.sqltoy.model.*;");
        source.append("import java.util.*;");
        source.append("import java.util.stream.Collectors;");
        source.append("@SuppressWarnings(\"unchecked\")");
        source.append("public class "+className);
        source.append(" extends org.noear.solon.extend.sqltoy.mapper.AbstractMapper");
        source.append(" implements "+type.getName()+"{");

        for(Method method:type.getDeclaredMethods()){
            if(method.isDefault()){
                continue;
            }
            buildMethod(method);
        }

        source.append("}");
    }
    public String getSource(){
        return source.toString();
    }
    private void buildMethod(Method method){
        source.append("public ");
        Class retType= method.getReturnType();
        boolean retVoid=false;
        if(Void.TYPE.equals(retType)){
            source.append("void");
            retVoid=true;
        }else{
            source.append(method.getReturnType().getName());
        }
        source.append(" ");
        source.append(method.getName());
        source.append("(");
        boolean first=true;
        for(Parameter p: method.getParameters()){
            if(first){
                first=false;
            }else{
                source.append(",");
            }
            source.append(p.toString());
        }
        source.append("){");
        Sql sql = method.getAnnotation(Sql.class);
        String sqlIdOrSql = method.getName();
        if (sql != null) {
            sqlIdOrSql = sql.value().trim();
        }
        SqlType sqlType = SqlType.search;
        int spaceIndex = sqlIdOrSql.indexOf(" ");
        if (spaceIndex == -1) {//有任意空白都认为不是ID
            try {
                SqlToyConfig cfg = context.getScriptLoader().getSqlConfig(sqlIdOrSql, null, "");
                if (cfg == null) {
                    throw new IllegalArgumentException("请检查 sqlId \"" + sqlIdOrSql + "\" 是否存在!");
                }
                String rawSql = cfg.getSql();
                spaceIndex = rawSql.indexOf(" ");
                sqlType = SqlType.getSqlType(rawSql.substring(0, spaceIndex));
            }catch (Exception e){
                e.printStackTrace();
                throw new IllegalArgumentException("请检查 sqlId \"" + sqlIdOrSql + "\" 是否存在!");
            }
        } else {
            sqlType = SqlType.getSqlType(sqlIdOrSql.substring(0, spaceIndex));
        }

        Class[] paramTypes = method.getParameterTypes();
        Parameter[] methodParameters = method.getParameters();

        int _pageIdx = -1;//有没有Page参数
        int _mapParamIdx = -1;//查询参数是否用Map
        int _objIdx = -1;
        int _listIdx = -1;
        Map<String,Integer> namedParamIdxMap=new HashMap<>();
        for (int i = 0; i < paramTypes.length; i++) {
            Class paramType=paramTypes[i];
            if (Page.class.isAssignableFrom(paramType)) {
                _pageIdx = i;
            } else if (Map.class.isAssignableFrom(paramType)) {
                _mapParamIdx = i;
            } else if (List.class.isAssignableFrom(paramType)) {
                _listIdx = i;
            } else {
                _objIdx = i;
            }
            Param param= methodParameters[i].getAnnotation(Param.class);
            if(param!=null){
                String paramName=param.value();
                if(StringUtil.isBlank(paramName)){
                    paramName=methodParameters[i].getName();
                }
                namedParamIdxMap.put(paramName,i);
            }
        }
        boolean hasNamedParams=namedParamIdxMap.size()>0;
        if(hasNamedParams){
            _objIdx=-1;
            _mapParamIdx=-1;
        }
        if(_objIdx>-1){
            if(!Serializable.class.isAssignableFrom(paramTypes[_objIdx])){
                throw new IllegalArgumentException(type.getName()+"#"+method.getName()+"中参数:"+methodParameters[_objIdx].getName()+"应为Serializable！");
            }
        }
        source.append("String _sql=\""+sqlIdOrSql+"\";");
        switch (sqlType) {
            case search:
                //分页查询
                if (Page.class.isAssignableFrom(retType)) {
                    int pageIdx = _pageIdx;
                    int mapParamIdx = _mapParamIdx;
                    int objIdx = _objIdx;
                    Class entityType = getGenericType(method);
                    boolean primitive = isPrimitive(entityType);
                    if (entityType == null || primitive) {
                        entityType = Map.class;
                    }
                    Class resultType = entityType;
                    String pageParam=pageIdx>-1?methodParameters[pageIdx].getName():"new Page()";
                    if (_objIdx > -1) {//使用Object bean 查询参数
                        String qeName=methodParameters[objIdx].getName();
                        source.append("QueryExecutor queryExecutor="+qeName+"==null?new QueryExecutor(_sql):new QueryExecutor(_sql,"+qeName+");");
                        source.append("queryExecutor.resultType("+resultType.getName()+".class);");
                        source.append("Page _result = dao.findPageByQuery("+pageParam+",queryExecutor).getPageResult();");
                        if(primitive){
                            appendPagePrimitive();
                        }
                        source.append("return _result;");
                    } else {
                        String mp=mapParamIdx>-1?methodParameters[mapParamIdx].getName():"new HashMap<>();";
                        source.append(" Map<String, Object> _queryMap = "+mp);
                        if(hasNamedParams){
                            for(String k:namedParamIdxMap.keySet()){
                                source.append("_queryMap.put(\""+k+"\","+k+");");
                            }
                        }
                        source.append(" QueryExecutor queryExecutor = new QueryExecutor(_sql, queryMap);");
                        source.append(" queryExecutor.resultType("+resultType.getName()+".class);");
                        source.append(" Page _result = dao.findPageByQuery("+pageParam+", queryExecutor).getPageResult();");
                        if(primitive){
                            appendPagePrimitive();
                        }
                        source.append("return _result;");
                    }
                } else if (List.class.isAssignableFrom(retType)) {//返回列表
                    int mapParamIdx = _mapParamIdx;
                    int objIdx = _objIdx;
                    Class entityType = getGenericType(method);//(Class) method.getGenericReturnType();
                    boolean primitive = isPrimitive(entityType);
                    if (entityType == null || primitive) {
                        entityType = Map.class;
                    }
                    Class resultType = entityType;

                    if (_objIdx > -1) {
                        String qeName=methodParameters[objIdx].getName();
                        source.append("QueryExecutor queryExecutor="+qeName+"==null?new QueryExecutor(_sql):new QueryExecutor(_sql,"+qeName+");");
                        source.append("queryExecutor.resultType("+resultType.getName()+".class);");
                       if(primitive){
                           source.append("List<Map> _result = dao.findByQuery(queryExecutor).getRows();");
                           source.append("return _result.stream().map(it -> it.values().stream().findFirst().get()).collect(Collectors.toList());");
                       }else{
                           source.append("return dao.findByQuery(queryExecutor).getRows();");
                       }
                    } else {
                        String mp=mapParamIdx>-1?methodParameters[mapParamIdx].getName():"new HashMap<>();";
                        source.append(" Map<String, Object> _queryMap = "+mp);
                        if(hasNamedParams){
                            for(String k:namedParamIdxMap.keySet()){
                                source.append("_queryMap.put(\""+k+"\","+k+");");
                            }
                        }
                        source.append("QueryExecutor queryExecutor = new QueryExecutor(_sql, queryMap).resultType("+resultType.getName()+".class);");
                        if(primitive){
                            source.append("List<Map> _result = dao.findByQuery(queryExecutor).getRows();");
                            source.append("return _result.stream().map(it -> it.values().stream().findFirst().get()).collect(Collectors.toList());");
                        }else{
                            source.append("return dao.findByQuery(queryExecutor).getRows();");
                        }
                    }
                } else {//查询单值
                    if (_objIdx > -1) {
                        int objIdx = _objIdx;
                        String qeName=methodParameters[objIdx].getName();
                        source.append("QueryExecutor queryExecutor="+qeName+"==null?new QueryExecutor(_sql):new QueryExecutor(_sql,"+qeName+");");
                        boolean isPrimitive = isPrimitive(retType);
                        if(isPrimitive){
                            source.append(" queryExecutor.resultType(Map.class);");
                        }else{
                            source.append("queryExecutor.resultType("+retType.getName()+".class);");
                        }
                        source.append(" List _result = dao.findByQuery(queryExecutor).getRows();");
                        source.append(" if (_result == null || _result.size() == 0){return null;}");
                        source.append("Object _target = result.get(0);");
                        if(isPrimitive){
                            source.append("return ((Map) _target).values().stream().findFirst().get();");
                        }else{
                            source.append("return ("+retType.getName()+")_target;");
                        }
                    } else {
                        int mapParamIdx = _mapParamIdx;
                        String mp=mapParamIdx>-1?methodParameters[mapParamIdx].getName():"new HashMap<>();";
                        source.append(" Map<String, Object> _queryMap = "+mp);
                        if(hasNamedParams){
                            for(String k:namedParamIdxMap.keySet()){
                                source.append("_queryMap.put(\""+k+"\","+k+");");
                            }
                        }
                        source.append("QueryExecutor queryExecutor = new QueryExecutor(_sql,_queryMap);");
                        boolean isPrimitive = isPrimitive(retType);
                        if(isPrimitive){
                            source.append("queryExecutor.resultType(Map.class);");
                        }else{
                            source.append(" queryExecutor.resultType("+retType.getName()+".class);");
                        }
                        source.append("List _result = dao.findByQuery(queryExecutor).getRows();");
                        source.append("if (_result == null || _result.size() == 0) {return null;}");
                        if(isPrimitive){
                            source.append("return ((Map) _target).values().stream().findFirst().get()");
                        }else{
                            source.append("return ("+retType.getName()+")_target;");
                        }
                    }

                }
                break;
            case insert:
            case delete:
                if (_objIdx > -1) {
                    int objIdx = _objIdx;
                    String qeName=methodParameters[objIdx].getName();
                    source.append("return dao.executeSql(_sql,"+qeName+")");
                } else {
                    int mapParamIdx = _mapParamIdx;
                    String mp=mapParamIdx>-1?methodParameters[mapParamIdx].getName():"new HashMap<>();";
                    source.append(" Map<String, Object> _queryMap = "+mp);
                    if(hasNamedParams){
                        for(String k:namedParamIdxMap.keySet()){
                            source.append("_queryMap.put(\""+k+"\","+k+");");
                        }
                    }
                    source.append("return dao.executeSql(_sql,_queryMap);");
                }
                break;
            case update:
                if (_listIdx > -1) {// batch update
                    int listIdx = _listIdx;
                    String lName=methodParameters[listIdx].getName();
                    source.append("return dao.batchUpdate(_sql,"+lName+");");
                } else if (_objIdx > -1) {
                    int objIdx = _objIdx;
                    String lName=methodParameters[objIdx].getName();
                    source.append("return dao.executeSql(_sql,"+lName+");");
                } else {
                    int mapParamIdx = _mapParamIdx;
                    String mp=methodParameters[mapParamIdx].getName();
                    source.append(" Map<String, Object> _queryMap = "+mp);
                    if(hasNamedParams){
                        for(String k:namedParamIdxMap.keySet()){
                            source.append("_queryMap.put(\""+k+"\","+k+");");
                        }
                    }
                    source.append("return dao.executeSql(_sql,_queryMap);");
                }
                break;
            default:
                source.append("return null;");
        }
        source.append("}");
    }
    private void appendPagePrimitive(){
        source.append("List<Map> _rows = _result.getRows();");
        source.append("List _distRows = _rows.stream().map(it -> it.values().stream().findFirst().get()).collect(Collectors.toList());");
        source.append("_result.setRows(_distRows);");
    }
    public Class compile(){
        // 声明包名：package top.fomeiherz;

        // 全类名：top.fomeiherz.Main
        String fullName = String.format("%s.%s", packageName, className);
        // 编译器
        JavaStringCompiler compiler = new JavaStringCompiler();
        try {
            // 编译
            Map<String, byte[]> results = compiler.compile(className + ".java", source.toString());
            // 加载内存中byte到Class<?>对象
            Class<?> clazz = compiler.loadClass(fullName, results);
            return clazz;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
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
            Integer.class,
            Byte.class,
            Double.class,
            Float.class,
            Character.class
    }));

    /**
     * 是否为原生类型，包括String,Date等
     *
     * @param retType
     * @return
     */
    private static boolean isPrimitive(Class retType) {
        if (retType == null) {
            return false;
        }
        return retType.isPrimitive() || primitiveTypes.contains(retType);
    }
}
