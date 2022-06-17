package de.indisopht.groovy

/**
 * @author rapper
 *
 */
import de.indisopht.serviceProvider.ServiceInterface
 
public class ServiceClass implements ServiceInterface {
    
    public Date getCurrentTime() {
        new Date(System.currentTimeMillis())
    }
    
}
