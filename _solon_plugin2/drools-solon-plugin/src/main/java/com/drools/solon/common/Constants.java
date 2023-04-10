package com.drools.solon.common;

/**
 * 基础常量
 *
 * @author <a href="mailto:hongwen0928@outlook.com">Karas</a>
 * @date 2020/3/10
 * @since 1.0.0
 */
public interface Constants {
    /**
     * 规则文件后缀
     */
    String SUFFIX_DRL = "drl";

    /**
     * 决策表后缀 excel 97-03版本的后缀 drools同样支持
     */
    String SUFFIX_EXCEL = "xls";
    
     /**
     * 决策表后缀 excel 2007版本以后的后缀 drools同样支持
     */
    String SUFFIX_EXCEL_2007 = "xlsx";

    /**
     * CSV后缀
     */
    String SUFFIX_CSV = "csv";

    /**
     * 开启监听器的标识符
     */
    String LISTENER_OPEN = "on";

    /**
     * 关闭监听器的标识符
     */
    String LISTENER_CLOSE = "off";

}
