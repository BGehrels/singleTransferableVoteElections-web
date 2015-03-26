package info.gehrels.voting.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public final class ServerStartedMessagePrintingListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {
	private static final Logger LOG = LoggerFactory.getLogger("ServerStarted");

	@Override
	public void onApplicationEvent(EmbeddedServletContainerInitializedEvent o) {
		LOG.info("Die Wahlauszählung kann beginnen. Die Eingabemaske ist nun unter den folgenden URLs erreichbar:");

		for (URI uri : getAllHostNameUrls(o.getEmbeddedServletContainer().getPort())) {
			LOG.info("{}", uri);
		}
	}

	private Set<URI> getAllHostNameUrls(int port) {
		Set<URI> hostnames = new HashSet<>();
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				hostnames.addAll(getAllHostNameUrlParts(en.nextElement(), port));
			}
		} catch (SocketException e) {
			LOG.error("Could not enumerate network interfaces", e);
		}
		return hostnames;
	}

	private Set<URI> getAllHostNameUrlParts(NetworkInterface networkInterface, int port) {
		Set<URI> hostNames = new HashSet<>();
		for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses(); enumIpAddress.hasMoreElements(); ) {
			hostNames.addAll(getUrlsFor(enumIpAddress.nextElement(), port));
		}
		return hostNames;
	}

	private Set<URI> getUrlsFor(InetAddress inetAddress, int port) {
		Set<URI> uris = new HashSet<>();
		uris.addAll(asUrl(inetAddress.getHostAddress(), port));
		uris.addAll(asUrl(inetAddress.getHostName(), port));
		uris.addAll(asUrl(inetAddress.getCanonicalHostName(), port));
		return uris;
	}

	private Collection<URI> asUrl(String hostAddress, int port) {
		Set<URI> result = new HashSet<>();
		try {
			result.add(new URI("http", null, hostAddress, port, "/", null, null));
		} catch (URISyntaxException e) {
			LOG.error("Could not build a url for host {}", hostAddress, e);
		}
		return result;
	}
}
