package org.noear.solon.swagger;

import java.util.Map;

import org.noear.solon.core.Props;

/**
 * 常量
 *
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/6/11
 */
public class SwaggerConst {
    /**
     * swagger.properties
     */
    public static Props CONFIG;

    /**
     * http返回状态
     */
    public static Map<Integer, String> HTTP_CODE;

    /**
     * http返回状态200时的通用返回格式
     */
    public static Class<?> COMMON_RES;

    /**
     * 返回值文档生成于data中
     */
    public static boolean RESPONSE_IN_DATA;
}