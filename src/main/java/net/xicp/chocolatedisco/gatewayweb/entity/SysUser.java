package net.xicp.chocolatedisco.gatewayweb.entity;

import lombok.Getter;
import lombok.Setter;
import net.xicp.chocolatedisco.gatewayweb.validator.group.AddValidationGroup;
import net.xicp.chocolatedisco.gatewayweb.validator.group.EditValidationGroup;
import net.xicp.chocolatedisco.gatewayweb.validator.group.QueryValidationGroup;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Pattern;


@Getter
@Setter
@Entity
public class SysUser {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank(message = "账号不能为空", groups = {AddValidationGroup.class})
    @Pattern(regexp = "\\w{1,32}", message = "账号不能包含非法字符", groups = {AddValidationGroup.class, EditValidationGroup.class, QueryValidationGroup.class})
    @Length(message = "账号长度不得超过32个字符", min = 1, max = 32, groups = {AddValidationGroup.class, EditValidationGroup.class, QueryValidationGroup.class})
    @Column(nullable = false, unique = true)
    private String account;
    @Column(nullable = false)
    private String password;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private SysRole sysRole;
    @Column(nullable = true)
    private Integer errorNumber;
    @Column(nullable = true)
    private Long errorTime;
}
