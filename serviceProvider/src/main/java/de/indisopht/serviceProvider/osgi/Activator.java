package de.indisopht.serviceProvider.osgi;

import org.apache.log4j.Logger;
import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.Peaberry;
import org.ops4j.peaberry.util.TypeLiterals;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

import de.indisopht.guice.groovy.GroovyGuice;
import de.indisopht.guice.groovy.GroovyProvider;
import de.indisopht.serviceProvider.ServiceInterface;

public class Activator implements BundleActivator {

    private static final Logger logger = Logger.getLogger(Activator.class);

    @Inject
    private Export<ServiceInterface> export;

    private Injector injector;

    public void start(BundleContext context) throws Exception {
        try {
            logger.info("starting " + Activator.class.getSimpleName());
            ServiceModule serviceModule = new ServiceModule();
            Module groovyModule = GroovyGuice.create(serviceModule)
                .forBundle(context.getBundle())
                .addClasspath(System.getProperty("GroovyOsgiSrcPath") == null ? System.getenv("GroovyOsgiSrcPath") : System.getProperty("GroovyOsgiSrcPath"))
                .resolveClasses(false)
                .build();
            injector = Guice.createInjector(Peaberry.osgiModule(context), groovyModule, serviceModule);
            injector.injectMembers(this);
            logger.info("started " + Activator.class.getSimpleName());
            logger.info("first time request: " + export.get().getCurrentTime());
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            t.printStackTrace();
        }
    }

    public void stop(BundleContext context) throws Exception {
        logger.info("stopping " + Activator.class.getSimpleName());
        if (export != null) {
            export.unget();
            export.unput();
        }
        logger.info("stopped " + Activator.class.getSimpleName());
    }

    static class ServiceModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(ServiceInterface.class).toProvider(new GroovyProvider<ServiceInterface>("de.indisopht.groovy.ServiceClass"){});
            bind(TypeLiterals.export(ServiceInterface.class)).toProvider(Peaberry.service(ServiceInterface.class).export());
        }
    }
}
