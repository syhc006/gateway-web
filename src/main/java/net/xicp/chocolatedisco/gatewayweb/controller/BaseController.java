package net.xicp.chocolatedisco.gatewayweb.controller;

import lombok.extern.slf4j.Slf4j;
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
    protected void copyNonNullProperties(Object src, Object target, String... ignoreProperties) {
        BeanWrapper srcBean = new BeanWrapperImpl(src);
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        Set<String> emptyName = new HashSet<>();
        for (String ip : ignoreProperties) {
            emptyName.add(ip);
        }
        for (PropertyDescriptor p : pds) {
            if (emptyName.contains(p.getName()) == false) {
                Object srcValue = srcBean.getPropertyValue(p.getName());
                if (srcValue == null) {
                    emptyName.add(p.getName());
                }
            }
        }

        String[] result = new String[emptyName.size()];
        BeanUtils.copyProperties(src, target, emptyName.toArray(result));
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