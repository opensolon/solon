package org.tio.solon.integration;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.intf.GroupListener;
import org.tio.core.stat.IpStatListener;
import org.tio.server.TioServer;
import org.tio.server.TioServerConfig;
import org.tio.server.intf.TioServerHandler;
import org.tio.server.intf.TioServerListener;
import org.tio.solon.config.TioServerProperties;
import org.tio.solon.config.TioServerSslProperties;

public final class TioServerBootstrap {

	private static final Logger logger = LoggerFactory.getLogger(TioServerBootstrap.class);

	private static final String GROUP_CONTEXT_NAME = "tio-server-solon";

	private final TioServerProperties serverProperties;
	private final TioServerSslProperties serverSslProperties;
	private TioServer tioServer;
	private final TioServerHandler serverAioHandler;

	private final IpStatListener ipStatListener;
	private final GroupListener groupListener;
	private final TioServerListener serverAioListener;

	private TioServerConfig serverTioConfig;
	
	private static volatile boolean running = false;

	public TioServerBootstrap(TioServerProperties serverProperties, TioServerSslProperties serverSslProperties,
			IpStatListener ipStatListener, GroupListener groupListener, TioServerHandler serverAioHandler,
			TioServerListener serverAioListener) {
		this.serverProperties = serverProperties;
		this.serverSslProperties = serverSslProperties;

		logger.debug(serverSslProperties.toString());

		this.ipStatListener = ipStatListener;
		this.groupListener = groupListener;
		this.serverAioListener = serverAioListener;
		this.serverAioHandler = serverAioHandler;
		afterSetProperties();
	}

	private void afterSetProperties() {
		if (this.serverAioHandler == null) {
			throw new TioServerMsgHandlerNotFoundException();
		}
		if (this.ipStatListener == null) {
			logger.warn("no bean type of IpStatListener found");
		}
		if (this.groupListener == null) {
			logger.warn("no bean type of GroupListener found");
		}
	}

	public TioServerConfig getServerTioConfig() {
		return serverTioConfig;
	}

	public void contextInitialized() {
		logger.info("initialize tio websocket server");
		try {
			initTioServerTioConfig();
			initTioServer();
//			start();
		} catch (Throwable e) {
			logger.error("Cannot bootstrap tio server :", e);
			throw new RuntimeException("Cannot bootstrap tio server :", e);
		}
	}

	private void initTioServer() {
		this.tioServer = new TioServer(serverTioConfig);
	}

	private void initTioServerTioConfig() {
		if(serverProperties.getName()==null) {
			serverProperties.setName(GROUP_CONTEXT_NAME);
		}
		serverTioConfig = new TioServerConfig(serverProperties.getName(), serverAioHandler, serverAioListener);
		if (ipStatListener != null) {
			serverTioConfig.setIpStatListener(ipStatListener);
			// fixed bug for IpStatListener not work
			serverTioConfig.ipStats.addDurations(serverProperties.getIpStatDurations());
		}
		if (serverAioListener != null) {
			serverTioConfig.setTioServerListener(serverAioListener);
		}
		if (groupListener != null) {
			serverTioConfig.setGroupListener(groupListener);
		}
		if (serverProperties.getHeartbeatTimeout() > 0) {
			serverTioConfig.setHeartbeatTimeout(serverProperties.getHeartbeatTimeout());
		}
		// ssl config
		if (serverSslProperties != null
				&& (serverSslProperties.getKeyStore() != null || serverSslProperties.getTrustStore() != null)
				&& serverSslProperties.getPassword() != null) {
			try {
				serverTioConfig.useSsl(serverSslProperties.getKeyStore(), serverSslProperties.getTrustStore(),
						serverSslProperties.getPassword());
			} catch (Exception e) {
				// catch and log
				logger.error("init ssl config error", e);
			}
		}
	}

	public void start() throws IOException {
		if(running) {
			logger.warn("[waring] tio server has been running!");
		}
		tioServer.start(serverProperties.getIp(), serverProperties.getPort());
		running = true;
	}

	public void stop() {
		if(running) {
			tioServer.stop();			
		}
		running =false;
	}
}
