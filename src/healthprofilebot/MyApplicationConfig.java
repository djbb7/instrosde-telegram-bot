package healthprofilebot;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("")
public class MyApplicationConfig extends ResourceConfig {
    public MyApplicationConfig () {
        packages("healthprofilebot"); // Jersey will load all the resources under this package
    }
}