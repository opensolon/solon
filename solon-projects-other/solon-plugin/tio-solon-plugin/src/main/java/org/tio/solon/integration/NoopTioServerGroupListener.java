package org.tio.solon.integration;

import org.tio.core.ChannelContext;
import org.tio.core.intf.GroupListener;

@Deprecated
class NoopTioServerGroupListener implements GroupListener{

	@Override
	public void onAfterBind(ChannelContext channelContext, String group) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAfterUnbind(ChannelContext channelContext, String group) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
