package org.tio.solon;

import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.tio.core.intf.GroupListener;
import org.tio.core.stat.IpStatListener;
import org.tio.solon.annotation.TioServerIpStatListenerAnno;

class TioServerIpStatListenerBeanBuilder implements BeanBuilder<TioServerIpStatListenerAnno> {

	@Override
	public void doBuild(Class<?> clz, BeanWrap bw, TioServerIpStatListenerAnno anno) throws Throwable {
		Object bean =  bw.raw();
		if(!(bean instanceof IpStatListener)) {
			bw = null;
			throw new IllegalArgumentException(clz.getName()+"must implements IpStatListener");
		}
		if (bw.clz().isInterface()) {
            throw new IllegalStateException("Interfaces are not supported as proxy components: " + bw.clz().getName());
        }
		TioServerBeanSet.ilisteners.add((IpStatListener)bean);
	}

}
