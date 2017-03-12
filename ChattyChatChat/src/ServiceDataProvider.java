import java.util.HashMap;
import java.util.Map;

/**
 * Container class used to map a class type to an instance
 * of the same type allowing for type-based lookups.
 * 
 * <br/>
 * 
 * This class only stores unique class types; in other words, if
 * a new instance is added for an existing type, it will replace the 
 * saved instance.
 */
public final class ServiceDataProvider {

	private Map<Class<?>, Object> services;
	
	public ServiceDataProvider() {
		services = new HashMap<Class<?>, Object>();
	}
	
	public ServiceDataProvider(ServiceDataProvider provider) {
		this();
		services.putAll(provider.services);
	}

	public void add_service(Class<?> serviceType, Object service) {
		services.put(serviceType, service);
	}
	
	public void remove_service(Class<?> serviceType) {
		services.remove(serviceType);
	}
	
	public <T extends Object> T get_service(Class<T> serviceType) {
		return serviceType.cast(services.get(serviceType));
	}
}
