package net.xicp.chocolatedisco.gatewayweb.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class AuthenticationFilter extends ZuulFilter {
    @Autowired
    public SimpleRouteLocator simpleRouteLocator;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext requestContext = getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        log.debug("request.getRequestURI: " + request.getRequestURI());
        log.debug("request.getServletPath: " + request.getServletPath());
        log.debug("request.getRequestURL: " + request.getRequestURL());
        String actualPath = getUri(request.getRequestURI());
        String targetLocation = getTargetLocation(request.getRequestURI());
        log.debug("actualPath: " + actualPath);
        log.debug("targetLocation: " + targetLocation);
        return null;
    }

    private RequestContext getCurrentContext() {
        return RequestContext.getCurrentContext();
    }

    private String getUri(String requestUri) {
        Route route = simpleRouteLocator.getMatchingRoute(requestUri);
        return route.getPath();
    }

    private String getTargetLocation(String requestUri) {
        Route route = simpleRouteLocator.getMatchingRoute(requestUri);
        return route.getLocation();
    }
}
