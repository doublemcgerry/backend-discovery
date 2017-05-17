package rz.thesis.core.modules.discovery;

import rz.thesis.core.modules.CoreModuleSettings;

public class DiscoveryModuleSettings extends CoreModuleSettings {
	private static final long serialVersionUID = -4199960068982042378L;

	public DiscoveryModuleSettings(int port, String discoveryMessage) {
		super();
		this.put("port", port);
		this.put("discoveryMessage", discoveryMessage);
	}

	public int getPort() {
		return (int) this.get("port");
	}

	public String getDiscoveryMessage() {
		return (String) this.get("discoveryMessage");
	}
}
