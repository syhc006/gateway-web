package net.xicp.chocolatedisco.gatewayweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
public class SysResource {
    private Long id;
    private String displayName;
    private String code;
    private String classification;
}
