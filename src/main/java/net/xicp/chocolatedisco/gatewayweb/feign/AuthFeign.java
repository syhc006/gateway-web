package net.xicp.chocolatedisco.gatewayweb.feign;

import net.xicp.chocolatedisco.gatewayweb.entity.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth")
public interface AuthFeign {
    @RequestMapping(value = "/sysUsers/findByAccount", method = RequestMethod.GET)
    SysUser findByAccount(@RequestParam("account") String account);
}
