package net.xicp.chocolatedisco.gatewayweb.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import net.xicp.chocolatedisco.gatewayweb.validator.group.AddValidationGroup;
import net.xicp.chocolatedisco.gatewayweb.validator.group.EditValidationGroup;
import net.xicp.chocolatedisco.gatewayweb.validator.group.QueryValidationGroup;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Set;


@Getter
@Setter
@Entity
public class SysRole {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank(message = "角色名不能为空", groups = {AddValidationGroup.class})
    @Pattern(regexp = "[^#%<>]{1,32}", message = "角色名不能包含非法字符", groups = {AddValidationGroup.class, EditValidationGroup.class, QueryValidationGroup.class})
    @Length(message = "角色名长度不得超过32个字符", min = 1, max = 32, groups = {AddValidationGroup.class, EditValidationGroup.class, QueryValidationGroup.class})
    @Column(nullable = false, unique = true)
    private String displayName;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sys_role_resource", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "resource_id")})
    private Set<SysResource> sysResources;
    @JsonIgnore
    @JSONField(serialize = false)
    @OneToMany(mappedBy = "sysRole")
    private Set<SysUser> sysUsers;
}
