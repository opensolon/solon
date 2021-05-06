package org.noear.solon.extend.shiro.impl;

import org.apache.shiro.web.servlet.ShiroFilter;

import javax.servlet.annotation.WebFilter;

/**
 * @author noear 2021/5/6 created
 */
@WebFilter("/*")
public class ShiroFilterImpl extends ShiroFilter {
}
