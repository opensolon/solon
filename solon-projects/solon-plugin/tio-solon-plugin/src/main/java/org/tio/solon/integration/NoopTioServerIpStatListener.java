package org.tio.solon.integration;

import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.intf.Packet;
import org.tio.core.stat.IpStat;
import org.tio.core.stat.IpStatListener;

@Deprecated
class NoopTioServerIpStatListener implements IpStatListener{

	@Override
	public void onExpired(TioConfig tioConfig, IpStat ipStat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect, IpStat ipStat)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDecodeError(ChannelContext channelContext, IpStat ipStat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess, IpStat ipStat)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize, IpStat ipStat)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes, IpStat ipStat) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet, IpStat ipStat, long cost)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
