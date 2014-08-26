package io.str8.turnpike.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum RouteRegister {
    instance;

    private Map<String, Route> routeMap = new HashMap<String, Route>();
    private final Logger LOG = LoggerFactory.getLogger(RouteRegister.class);

    public void addRoute(String serviceUri, Class serviceClass, Route route){
        this.routeMap.put(serviceUri, route);
        LOG.debug("Added Routes For ==> \t/{}\t==> {}", serviceUri, serviceClass.getName());
    }

    public Route getRoute(String serviceName){
        return routeMap.get(serviceName);
    }


}
