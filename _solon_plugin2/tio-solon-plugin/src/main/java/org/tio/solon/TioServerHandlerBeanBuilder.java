package org.tio.solon;

import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;
import org.tio.server.intf.TioServerHandler;
import org.tio.solon.annotation.TioServerHandlerAnno;

class TioServerHandlerBeanBuilder implements BeanBuilder<TioServerHandlerAnno> {

	@Override
	public void doBuild(Class<?> clz, BeanWrap bw, TioServerHandlerAnno anno) throws Throwable {
		Object bean =  bw.raw();
		if(!(bean instanceof TioServerHandler)) {
			bw = null;
			throw new IllegalArgumentException(clz.getName()+"must implements TioServerHandler");
		}
		if (bw.clz().isInterface()) {
            throw new IllegalStateException("Interfaces are not supported as proxy components: " + bw.clz().getName());
        }
		TioServerBeanSet.shandlers.add((TioServerHandler)bean);
//        int modifier = bw.clz().getModifiers();
//        if (Modifier.isFinal(modifier)) {
//            throw new IllegalStateException("Final classes are not supported as proxy components: " + bw.clz().getName());
//        }
//
//        if (Modifier.isAbstract(modifier)) {
//            throw new IllegalStateException("Abstract classes are not supported as proxy components: " + bw.clz().getName());
//        }
//
//        if (Modifier.isPublic(modifier) == false) {
//            throw new IllegalStateException("Not public classes are not supported as proxy components: " + bw.clz().getName());
//        }
	}

}
