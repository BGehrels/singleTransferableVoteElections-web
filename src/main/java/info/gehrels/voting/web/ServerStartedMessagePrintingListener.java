package info.gehrels.voting.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;

import java.net.InetAddress;
import java.net.UnknownHostException;

class ServerStartedMessagePrintingListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {
	private static final Logger LOG = LoggerFactory.getLogger("ServerStarted");

	@Override
	public void onApplicationEvent(EmbeddedServletContainerInitializedEvent o) {
		LOG.info("Die Wahlauszählung kann beginnen. Die Eingabemaske ist nun unter den folgenden URLs erreichbar:",
		         o.getSource().getPort());
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			logUrl(o, localHost.getCanonicalHostName());
			logUrl(o, localHost.getHostAddress());
			logUrl(o, localHost.getHostName());
		} catch (UnknownHostException e) {
			logUrl(o, "localhost");
			logUrl(o, "127.0.0.1");
		}
	}

	private void logUrl(EmbeddedServletContainerInitializedEvent o, String host) {
		LOG.info("http://{}:{}/", host, o.getEmbeddedServletContainer().getPort());
	}
}
