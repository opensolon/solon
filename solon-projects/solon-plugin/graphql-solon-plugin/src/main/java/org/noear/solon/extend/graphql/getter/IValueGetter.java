package org.noear.solon.extend.graphql.getter;

import graphql.schema.DataFetchingEnvironment;
import org.noear.solon.core.wrap.ParamWrap;

/**
 * @author fuzi1996
 * @since 2.3
 */
public interface IValueGetter {

    Object get(DataFetchingEnvironment environment, ParamWrap paramWrap);
}
