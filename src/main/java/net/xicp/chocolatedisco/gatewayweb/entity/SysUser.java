package net.xicp.chocolatedisco.gatewayweb.entity;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.stream.Collectors;


@Getter
@Setter
public class SysUser {
    private Long id;
    private String account;
    private String password;
    private SysRole sysRole;
    private Integer errorNumber;
    private Long errorTime;

    @JsonIgnore
    public boolean isAccountNonLocked() {
        if (errorNumber == null || errorTime == null) {
            return true;
        } else if (errorNumber >= 5 && System.currentTimeMillis() - errorTime <= 5 * 60 * 1000) {
            return false;
        } else {
            return true;
        }
    }

    @JsonIgnore
    public String getAuthorities() {
        return Optional.ofNullable(sysRole)
                .map(sysRole -> sysRole.getSysResources())
                .map(sysResources -> JSONArray.toJSONString(
                        sysResources.stream()
                                .map(sysResource -> sysResource.getClassification() + "|" + sysResource.getCode())
                                .collect(Collectors.toSet())
                ))
                .orElse("[]");
    }
}
