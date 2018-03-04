package net.xicp.chocolatedisco.gatewayweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
public class SysRole {
    private Long id;
    private String displayName;
    private Set<SysResource> sysResources;
}
