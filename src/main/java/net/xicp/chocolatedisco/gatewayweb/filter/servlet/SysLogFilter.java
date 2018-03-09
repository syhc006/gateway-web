package net.xicp.chocolatedisco.gatewayweb.filter.servlet;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import net.xicp.chocolatedisco.gatewayweb.entity.SysLog;
import net.xicp.chocolatedisco.gatewayweb.filter.servlet.wrapper.LoggingHttpServletRequestWrapper;
import net.xicp.chocolatedisco.gatewayweb.filter.servlet.wrapper.LoggingHttpServletResponseWrapper;
import net.xicp.chocolatedisco.gatewayweb.repository.SysLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Order(1)
@WebFilter(filterName = "syslogfilter", urlPatterns = "/*")
@Slf4j
public class SysLogFilter implements Filter {

    @Value("${gateway.token-header}")
    private String tokenHeader;

    @Value("${jwt.token.secret}")
    private String secret;

    @Autowired
    private SysLogRepository sysLogRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("LoggingFilter just supports HTTP requests");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String account = Optional.ofNullable(httpRequest.getHeader(tokenHeader))
                .map(token -> {
                    try {
                        Claims claims = Jwts.parser()
                                .setSigningKey(secret)
                                .parseClaimsJws(token)
                                .getBody();
                        return (String) claims.get("account");
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                }).orElse(null);
        if (account != null &&
                (httpRequest.getMethod().equals("POST") ||
                        httpRequest.getMethod().equals("PUT") ||
                        httpRequest.getMethod().equals("DELETE")
                )) {
            LoggingHttpServletRequestWrapper requestWrapper = new LoggingHttpServletRequestWrapper(httpRequest);
            LoggingHttpServletResponseWrapper responseWrapper = new LoggingHttpServletResponseWrapper(httpResponse);
            SysLog sysLog = new SysLog();
            sysLog.setAccount(account);
            sysLog.setGenerateTime(Instant.now().toEpochMilli());
            getRequestDescription(requestWrapper, sysLog);
            filterChain.doFilter(requestWrapper, responseWrapper);
            getResponseDescription(responseWrapper, sysLog);
            httpResponse.getOutputStream().write(responseWrapper.getContentAsBytes());
            try {
                sysLogRepository.saveAndFlush(sysLog);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            filterChain.doFilter(httpRequest, httpResponse);
        }
    }

    @Override
    public void destroy() {
    }

    protected void getRequestDescription(LoggingHttpServletRequestWrapper requestWrapper, SysLog log) {
        log.setIp(requestWrapper.getLocalAddr());
        log.setEvent(requestWrapper.getMethod());
        log.setUri(requestWrapper.getRequestURI());
        log.setParam(requestWrapper.getContent());
    }

    protected void getResponseDescription(LoggingHttpServletResponseWrapper responseWrapper, SysLog log) {
        log.setResult(String.valueOf(responseWrapper.getStatus()));
        log.setResponse(responseWrapper.getContent());
    }
}
