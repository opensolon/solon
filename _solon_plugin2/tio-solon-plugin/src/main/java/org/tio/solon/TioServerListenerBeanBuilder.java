package org.tio.solon;

import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.tio.core.stat.IpStatListener;
import org.tio.server.intf.TioServerListener;
import org.tio.solon.annotation.TioServerListenerAnno;

class TioServerListenerBeanBuilder implements BeanBuilder<TioServerListenerAnno> {

	@Override
	public void doBuild(Class<?> clz, BeanWrap bw, TioServerListenerAnno anno) throws Throwable {
		Object bean =  bw.raw();
		if(!(bean instanceof TioServerListener)) {
			bw = null;
			throw new IllegalArgumentException(clz.getName()+"must implements TioServerListener");
		}
		if (bw.clz().isInterface()) {
            throw new IllegalStateException("Interfaces are not supported as proxy components: " + bw.clz().getName());
        }
		TioServerBeanSet.slisteners.add((TioServerListener)bean);
	}

}
