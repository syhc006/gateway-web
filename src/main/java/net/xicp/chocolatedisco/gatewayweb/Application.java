package net.xicp.chocolatedisco.gatewayweb;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@SpringBootApplication(exclude = {})
@EnableJpaRepositories
@EnableZuulProxy
@EnableDiscoveryClient
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Slf4j
    public static class AuthenticationFilter extends ZuulFilter {

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
            try {
                RequestContext requestContext = getCurrentContext();
                HttpServletRequest request = requestContext.getRequest();

                String actualPath = getUri(request.getRequestURI());
                String targetLocation = getTargetLocation(request.getRequestURI());

                log.debug("request.getRequestURI: " + request.getRequestURI());
                log.debug("request.getServletPath: " + request.getServletPath());
                log.debug("request.getRequestURL: " + request.getRequestURL());
                log.debug("actualPath: " + actualPath);
                log.debug("targetLocation: " + targetLocation);

                /*if (targetLocation.equals(gateService.getOAuthServiceLocation()) && actualPath.equals(gateService.getOAuthAccessTokenUri())) {
                    return false;
                }*/

                String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                String token = authorizationHeader.substring("Bearer".length()).trim();
                if (token.isEmpty() || token.equals("")) {
                    return true;
                }

                log.debug("finding accesstoken from header: " + token);

//                return gateService.shouldFilter(token, actualPath);
                return false;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return true;
            }
        }

        @Override
        public Object run() {
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
}
