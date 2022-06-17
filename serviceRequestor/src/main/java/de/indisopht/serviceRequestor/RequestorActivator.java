package de.indisopht.serviceRequestor;

import org.apache.log4j.Logger;
import org.ops4j.peaberry.Peaberry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import de.indisopht.serviceProvider.ServiceInterface;

public class RequestorActivator implements BundleActivator {

    private static final Logger logger = Logger.getLogger(RequestorActivator.class);

    @Inject
    private ServiceInterface osgiService;

    private Injector injector;

    public void start(BundleContext arg0) throws Exception {
        logger.info("starting " + RequestorActivator.class.getSimpleName());
        injector = Guice.createInjector(Peaberry.osgiModule(arg0), new ServiceModule());
        injector.injectMembers(this);
        logger.info(RequestorActivator.class.getSimpleName() + " started");
        logger.info("service answers: " + osgiService.getCurrentTime());
    }

    public void stop(BundleContext arg0) throws Exception {
        logger.info("stopped " + RequestorActivator.class.getSimpleName());
    }

    static class ServiceModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(ServiceInterface.class).toProvider(Peaberry.service(ServiceInterface.class).single());
        }

    }
}
