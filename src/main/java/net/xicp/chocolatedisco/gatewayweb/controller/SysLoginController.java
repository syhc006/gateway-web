package net.xicp.chocolatedisco.gatewayweb.controller;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.xicp.chocolatedisco.gatewayweb.entity.SysUser;
import net.xicp.chocolatedisco.gatewayweb.exception.ErrorAuthenticationException;
import net.xicp.chocolatedisco.gatewayweb.exception.ErrorAuthorityException;
import net.xicp.chocolatedisco.gatewayweb.feign.AuthFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/gateway")
public class SysLoginController extends BaseController {

    @Autowired
    private AuthFeign authFeign;

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expiration}")
    private Long expiration;

    @Value("${jwt.token.issuer}")
    private String issuer;

    @PostMapping("token")
    public String getAccessToken(@RequestParam String account, @RequestParam String password) {
        SysUser sysUser = Optional.ofNullable(authFeign.findByAccount(account))
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(ErrorAuthenticationException::new);
        if (sysUser.getSysRole() == null) {
            throw new ErrorAuthorityException();
        }
        return createJWT(issuer, expiration, secret, sysUser);
    }

    private static String createJWT(String issuer, long expiration, String base64Security, SysUser sysUser) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                .claim("role", Optional.ofNullable(sysUser.getSysRole()).map(sysRole -> sysRole.getDisplayName()).orElse(""))
                .claim("account", sysUser.getAccount())
                .claim("resources", sysUser.getAuthorities())
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setAudience(sysUser.getAccount())
                .signWith(signatureAlgorithm, signingKey);
        //添加Token过期时间
        if (expiration >= 0) {
            long expMillis = nowMillis + expiration;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        //生成JWT
        return builder.compact();
    }
}
