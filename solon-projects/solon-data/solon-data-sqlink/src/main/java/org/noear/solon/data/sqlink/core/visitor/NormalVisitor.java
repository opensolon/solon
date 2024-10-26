package org.noear.solon.data.sqlink.core.visitor;


import org.noear.solon.data.sqlink.base.IConfig;

public class NormalVisitor extends SqlVisitor
{
    public NormalVisitor(IConfig config)
    {
        super(config);
    }

    public NormalVisitor(IConfig config, int offset)
    {
        super(config, offset);
    }

    @Override
    protected NormalVisitor getSelf()
    {
        return new NormalVisitor(config);
    }
}
