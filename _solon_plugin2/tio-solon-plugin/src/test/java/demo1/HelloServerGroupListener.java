package demo1;

import org.tio.core.ChannelContext;
import org.tio.core.intf.GroupListener;
import org.tio.solon.annotation.TioServerGroupListenerAnno;

/**
 * Tio group 监听，需要实现 {@link GroupListener} 接口
 * 通过加 {@link TioGroupListener} 注解启用，否则不会启用
 * Note: Bean 的名称不能改动，否则无法注入
 *
 * @author yangjian
 */
@TioServerGroupListenerAnno
public class HelloServerGroupListener implements GroupListener {
    @Override
    public void onAfterBind(ChannelContext channelContext, String s) throws Exception {

    }

    @Override
    public void onAfterUnbind(ChannelContext channelContext, String s) throws Exception {

    }
}
