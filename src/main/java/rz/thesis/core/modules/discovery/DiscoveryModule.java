package rz.thesis.core.modules.discovery;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rz.thesis.core.Core;
import rz.thesis.core.modules.CoreModule;
import rz.thesis.core.modules.ServiceDefinition;

public class DiscoveryModule extends CoreModule implements DiscoveryModuleThread.Callbacks {
	private static final Logger LOGGER = Logger.getLogger(DiscoveryModule.class.getName());
	DiscoveryModuleThread thread;
	private final int discoveryPort;
	private final String discoveryMessage;

	public DiscoveryModule(Core core, DiscoveryModuleSettings settings) {
		super(DiscoveryModule.class.getSimpleName(), core, settings);
		this.discoveryPort = settings.getPort();
		this.discoveryMessage = settings.getDiscoveryMessage();
	}

	@Override
	public void initializeModule() {
		this.thread = new DiscoveryModuleThread(discoveryPort, discoveryMessage, this, getCore());
	}

	@Override
	public void startModule() {
		this.thread.startServer();
	}

	@Override
	public void stopModule() {
		this.thread.stopServer();
	}

	@Override
	public List<ServiceDefinition> getServiceDefinition() {
		List<ServiceDefinition> sds = new ArrayList<>();
		sds.add(new ServiceDefinition("Discovery Server", 19000));
		return sds;
	}

	@Override
	public void onClientConnected(InetAddress address, int port) {
		LOGGER.debug("Client requested services info on address : " + address.getHostAddress() + ":" + port);
	}

	@Override
	public void onStart() {
		LOGGER.debug("Discovery server started on port:" + discoveryPort);
	}

	@Override
	public void onStop() {
		LOGGER.debug("Discovery server stopped on port:" + discoveryPort);
	}

	@Override
	public void onProgress(String message) {
		LOGGER.debug(message);
	}

}
