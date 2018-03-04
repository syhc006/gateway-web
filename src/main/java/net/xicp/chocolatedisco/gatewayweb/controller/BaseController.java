package net.xicp.chocolatedisco.gatewayweb.controller;

import lombok.extern.slf4j.Slf4j;
import net.xicp.chocolatedisco.gatewayweb.exception.ErrorAuthenticationException;
import net.xicp.chocolatedisco.gatewayweb.exception.ErrorAuthorityException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.beans.PropertyDescriptor;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class BaseController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private Map runtimeExceptionHandler(ErrorAuthenticationException e) {
        log.error(e.getMessage(), e);
        Map result = new HashMap<>();
        result.put("timestamp", Instant.now().getEpochSecond());
        result.put("status", HttpStatus.UNAUTHORIZED.value());
        result.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        result.put("message", e.getMessage());
        result.put("exception_class", e.getClass().getCanonicalName());
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private Map runtimeExceptionHandler(ErrorAuthorityException e) {
        log.error(e.getMessage(), e);
        Map result = new HashMap<>();
        result.put("timestamp", Instant.now().getEpochSecond());
        result.put("status", HttpStatus.FORBIDDEN.value());
        result.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        result.put("message", e.getMessage());
        result.put("exception_class", e.getClass().getCanonicalName());
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private Map runtimeExceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        Map result = new HashMap<>();
        result.put("timestamp", Instant.now().getEpochSecond());
        result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        result.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        result.put("message", e.getMessage());
        result.put("exception_class", e.getClass().getCanonicalName());
        return result;
    }

}