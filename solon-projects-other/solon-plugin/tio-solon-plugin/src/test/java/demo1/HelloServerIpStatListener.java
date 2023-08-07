package demo1;

import org.noear.solon.annotation.Component;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.intf.Packet;
import org.tio.core.stat.IpStat;
import org.tio.core.stat.IpStatListener;

/**
 * IP 统计监听，需要实现 {@link IpStatListener} 接口
 * 通过加 {@link TioIpStatListener} 注解启用，否则不会启用
 * Note: Bean 的名称不能改动，否则无法注入
 *
 * @author yangjian
 */
@Component
public class HelloServerIpStatListener implements IpStatListener {

    @Override
    public void onAfterConnected(ChannelContext channelContext, boolean b, boolean b1, IpStat ipStat) throws Exception {

    }

    @Override
    public void onDecodeError(ChannelContext channelContext, IpStat ipStat) {

    }

    @Override
    public void onAfterSent(ChannelContext channelContext, Packet packet, boolean b, IpStat ipStat) throws Exception {

    }

    @Override
    public void onAfterDecoded(ChannelContext channelContext, Packet packet, int i, IpStat ipStat) throws Exception {

    }

    @Override
    public void onAfterReceivedBytes(ChannelContext channelContext, int i, IpStat ipStat) throws Exception {
        System.out.println("ipStat : " + ipStat.getIp());
    }

    @Override
    public void onAfterHandled(ChannelContext channelContext, Packet packet, IpStat ipStat, long l) throws Exception {

    }

	@Override
	public void onExpired(TioConfig tioConfig, IpStat ipStat) {
		// TODO Auto-generated method stub
		
	}
}
