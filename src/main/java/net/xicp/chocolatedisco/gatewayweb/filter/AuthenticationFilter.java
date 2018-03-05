package net.xicp.chocolatedisco.gatewayweb.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class AuthenticationFilter extends ZuulFilter {
    @Autowired
    private SimpleRouteLocator simpleRouteLocator;
    @Value("${gateway.host}")
    private String gatewayHost;
    @Value("${gateway.token-uri}")
    private String tokenUri;
    @Value("${gateway.token-header}")
    private String tokenHeader;
    @Value("${jwt.token.secret}")
    private String secret;

    @Autowired
    private AntPathMatcher antPathMatcher;

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
        RequestContext requestContext = getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String actualPath = getUri(request.getRequestURI());
        String targetLocation = getTargetLocation(request.getRequestURI());
        if (targetLocation.equals(gatewayHost) && actualPath.equals(tokenUri)) {
            return false;
        }
        return true;
    }

    @Override
    public Object run() {
        RequestContext requestContext = getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String actualPath = getUri(request.getRequestURI());
        String targetLocation = getTargetLocation(request.getRequestURI());
        log.debug("request.getRequestURI: " + request.getRequestURI());
        log.debug("request.getServletPath: " + request.getServletPath());
        log.debug("request.getRequestURL: " + request.getRequestURL());
        log.debug("actualPath: " + actualPath);
        log.debug("targetLocation: " + targetLocation);
        String token = request.getHeader(tokenHeader);
        boolean authenticationError = Optional.ofNullable(token).map(t -> t.trim().equals("")).orElse(true);
        if (authenticationError == true) {
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            Map result = new HashMap<>();
            result.put("timestamp", Instant.now().getEpochSecond());
            result.put("status", HttpStatus.UNAUTHORIZED.value());
            result.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            requestContext.setResponseBody(JSON.toJSONString(result));
            return null;
        }
        if (validResources(request) == false) {
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
            Map result = new HashMap<>();
            result.put("timestamp", Instant.now().getEpochSecond());
            result.put("status", HttpStatus.FORBIDDEN.value());
            result.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
            requestContext.setResponseBody(JSON.toJSONString(result));
            return null;
        }
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

    private boolean validResources(HttpServletRequest request) {
        List<String> resources = getResourceFromToken(request.getHeader("Authentication"));
        String target = request.getRequestURI().replace("/" + getTargetLocation(request.getRequestURI()), "");
        String requestMethod = request.getMethod();
        for (String resource : resources) {
            resource = resource.replace(requestMethod + "|", "");
            if (antPathMatcher.match(resource, target)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getResourceFromToken(String token) {
        List<String> resources;
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        resources = JSONArray.parseArray((String) claims.get("resources"), String.class);
        return resources;
    }

}
