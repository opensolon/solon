package org.tio.solon.integration;

import java.nio.ByteBuffer;

import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.exception.TioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.TioServerHandler;

/**
 * 默认无任何操作的处理
 * @author Administrator
 *
 */
@Deprecated
class NoopTioServerHandler implements TioServerHandler{

	@Override
	public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext)
			throws TioDecodeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
