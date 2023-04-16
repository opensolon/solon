package org.tio.solon;

import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.tio.core.intf.GroupListener;
import org.tio.solon.annotation.TioServerGroupListenerAnno;

class TioServerGroupListenerBeanBuilder implements BeanBuilder<TioServerGroupListenerAnno> {

	@Override
	public void doBuild(Class<?> clz, BeanWrap bw, TioServerGroupListenerAnno anno) throws Throwable {
		Object bean =  bw.raw();
		if(!(bean instanceof GroupListener)) {
			bw = null;
			throw new IllegalArgumentException(clz.getName()+"must implements GroupListener");
		}
		if (bw.clz().isInterface()) {
			bw = null;
            throw new IllegalStateException("Interfaces are not supported as proxy components: " + bw.clz().getName());
        }
		
		TioServerBeanSet.glisteners.add((GroupListener)bean);
	}

}
