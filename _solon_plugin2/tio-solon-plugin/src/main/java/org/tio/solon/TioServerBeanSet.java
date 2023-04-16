package org.tio.solon;

import java.util.ArrayList;
import java.util.List;

import org.tio.core.intf.GroupListener;
import org.tio.core.stat.IpStatListener;
import org.tio.server.intf.TioServerHandler;
import org.tio.server.intf.TioServerListener;

interface TioServerBeanSet {

	List<TioServerHandler> shandlers = new ArrayList<>();
	List<TioServerListener> slisteners = new ArrayList<>();
	List<IpStatListener> ilisteners = new ArrayList<>();
	List<GroupListener> glisteners = new ArrayList<>();
	
	static void clear() {
		shandlers.clear();
		slisteners.clear();
		ilisteners.clear();
		glisteners.clear();
	}
	
}
