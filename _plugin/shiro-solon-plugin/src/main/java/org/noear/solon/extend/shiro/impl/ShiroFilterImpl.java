package org.noear.solon.extend.shiro.impl;

import org.apache.shiro.web.servlet.ShiroFilter;

import javax.servlet.annotation.WebFilter;

/**
 * @author noear
 * @since 1.3
 */
@WebFilter("/*")
public class ShiroFilterImpl extends ShiroFilter {
}
