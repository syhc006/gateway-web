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
public class SysResource {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank(message = "资源名不能为空", groups = {AddValidationGroup.class})
    @Pattern(regexp = "[^#%<>]{1,32}", message = "资源名不能包含非法字符", groups = {AddValidationGroup.class, EditValidationGroup.class, QueryValidationGroup.class})
    @Length(message = "资源名长度不得超过32个字符", min = 1, max = 32, groups = {AddValidationGroup.class, EditValidationGroup.class, QueryValidationGroup.class})
    @Column(nullable = false, unique = true)
    private String displayName;
    @NotBlank(message = "资源码不能为空", groups = {AddValidationGroup.class})
    @Pattern(regexp = "[^#%<>]{1,32}", message = "资源码不能包含非法字符", groups = {AddValidationGroup.class, EditValidationGroup.class, QueryValidationGroup.class})
    @Length(message = "资源码长度不得超过32个字符", min = 1, max = 32, groups = {AddValidationGroup.class, EditValidationGroup.class, QueryValidationGroup.class})
    @Column(nullable = false, unique = true)
    private String code;
    @NotBlank(message = "分类不能为空", groups = {AddValidationGroup.class})
    @Pattern(regexp = "[^#%<>]{1,32}", message = "分类不能包含非法字符", groups = {AddValidationGroup.class, EditValidationGroup.class, QueryValidationGroup.class})
    @Length(message = "分类长度不得超过32个字符", min = 1, max = 32, groups = {AddValidationGroup.class, EditValidationGroup.class, QueryValidationGroup.class})
    @Column(nullable = false)
    private String classification;
    @JsonIgnore
    @JSONField(serialize = false)
    @ManyToMany
    @JoinTable(name = "sys_role_resource", joinColumns = {@JoinColumn(name = "resource_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<SysRole> sysRoles;

}
