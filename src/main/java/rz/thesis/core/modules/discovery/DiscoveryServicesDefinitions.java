package rz.thesis.core.modules.discovery;

import java.io.Serializable;
import java.util.List;

import rz.thesis.core.modules.ServiceDefinition;

/**
 * This is a container for the services definitions for this instance of webvis server
 * @author achelius
 *
 */
public class DiscoveryServicesDefinitions implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -417619365902581094L;
	private List<ServiceDefinition> services;
	public DiscoveryServicesDefinitions(List<ServiceDefinition> services) {
		this.setServices(services);
	}
	public List<ServiceDefinition> getServices() {
		return services;
	}
	public void setServices(List<ServiceDefinition> services) {
		this.services = services;
	}
}
