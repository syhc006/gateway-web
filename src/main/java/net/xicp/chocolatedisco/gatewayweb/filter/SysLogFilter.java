package net.xicp.chocolatedisco.gatewayweb.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import net.xicp.chocolatedisco.gatewayweb.entity.SysLog;
import net.xicp.chocolatedisco.gatewayweb.repository.SysLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by SunYu on 2018/3/6.
 */
@Component
@Slf4j
public class SysLogFilter extends ZuulFilter {

    @Value("${gateway.token-header}")
    private String tokenHeader;
    @Value("${jwt.token.secret}")
    private String secret;
    @Autowired
    private SysLogRepository sysLogRepository;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1000;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        Optional.ofNullable(request.getHeader(tokenHeader))
                .map(token -> {
                    boolean error = false;
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
                })
                .ifPresent(accout -> {
                    String param = null;
                    String response = null;
                    if (request.getMethod().equals(HttpMethod.POST.toString()) ||
                            request.getMethod().equals(HttpMethod.PUT.toString())) {
                        try {
                            param = readString(request.getInputStream());
                            ByteArrayOutputStream output = createOutput(context.getResponseDataStream());
                            context.setResponseDataStream(createInput(output));
                            response = readString(createInput(output));

                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    if (request.getMethod().equals(HttpMethod.GET.toString()) == false) {
                        log.debug("account: " + accout);
                        log.debug("request.method: " + request.getMethod());
                        log.debug("request.getRequestURI: " + request.getRequestURI());
                        log.debug("request.getServletPath: " + request.getServletPath());
                        log.debug("request.getRequestURL: " + request.getRequestURL());
                        log.debug("request.remoteAddr: " + request.getRemoteAddr());
                        log.debug("request.param: " + param);
                        log.debug("response:" + response);
                        log.debug("response.status: " + context.getResponseStatusCode());
                        SysLog log = new SysLog();
                        log.setAccount(accout);
                        log.setEvent(request.getMethod());
                        log.setGenerateTime(Instant.now().toEpochMilli());
                        log.setIp(request.getRemoteAddr());
                        log.setParam(param);
                        log.setResponse(response);
                        log.setResult(String.valueOf(context.getResponseStatusCode()));
                        log.setUri(request.getRequestURL().toString());
                        sysLogRepository.saveAndFlush(log);
                    }
                });
        return null;
    }

    private ByteArrayOutputStream createOutput(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int b;
        try {
            while ((b = input.read()) > 0) {
                output.write(b);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return output;
    }

    private ByteArrayInputStream createInput(ByteArrayOutputStream output) {
        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        return input;
    }

    private String readString(InputStream input) {
        return new BufferedReader(new InputStreamReader(input))
                .lines().map(line -> line.trim()).collect(Collectors.joining(""));
    }
}
