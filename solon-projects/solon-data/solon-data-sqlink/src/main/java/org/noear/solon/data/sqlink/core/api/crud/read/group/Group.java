package org.noear.solon.data.sqlink.core.api.crud.read.group;


public class Group<Key, T> extends SqlAggregation<T> implements IGroup
{
    public Key key;
}
