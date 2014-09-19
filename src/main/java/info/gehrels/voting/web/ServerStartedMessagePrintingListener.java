package info.gehrels.voting.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;

class ServerStartedMessagePrintingListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {
	private static final Logger LOG = LoggerFactory.getLogger("ServerStarted");

	@Override
	public void onApplicationEvent(EmbeddedServletContainerInitializedEvent o) {
		LOG.info("Die Wahlauszählung kann beginnen. Die Eingabemaske ist nun unter http://localhost:{}/ erreichbar.", o.getSource().getPort());
	}
}
