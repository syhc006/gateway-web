package net.xicp.chocolatedisco.gatewayweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by SunYu on 2018/3/6.
 */
@Getter
@Setter
@Entity
public class SysLog {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String account;
    @Column(nullable = false)
    private String event;//请求操作
    @Column(nullable = false)
    private Long generateTime;//请求时间
    @Column(nullable = false)
    private String ip;//客户端请求IP
    @Column(nullable = false)
    private String uri;//客户端请求路径
    @Lob
    private String param;//请求参数
    @Lob
    private String response;//请求响应
    @Column(nullable = false)
    private String result;//响应码
}
