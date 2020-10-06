/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: NumberUtils.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/
package org.smartboot.http.utils;

/**
 * <p>Provides extra functionality for Java Number classes.</p>
 *
 * @version $Id: NumberUtils.java 1507169 2013-07-26 01:03:52Z sebb $
 * @since 2.0
 */
public class NumberUtils {


    /**
     * <p><code>NumberUtils</code> instances should NOT be constructed in standard programming.
     * Instead, the class should be used as <code>NumberUtils.toInt("6");</code>.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean instance
     * to operate.</p>
     */
    public NumberUtils() {
        super();
    }

    //-----------------------------------------------------------------------


    /**
     * <p>Convert a <code>String</code> to an <code>int</code>, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string is <code>null</code>, the default value is returned.</p>
     *
     * <pre>
     *   NumberUtils.toInt(null, 1) = 1
     *   NumberUtils.toInt("", 1)   = 1
     *   NumberUtils.toInt("1", 0)  = 1
     * </pre>
     *
     * @param str          the string to convert, may be null
     * @param defaultValue the default value
     * @return the int represented by the string, or the default if conversion fails
     * @since 2.1
     */
    public static int toInt(final String str, final int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

}
