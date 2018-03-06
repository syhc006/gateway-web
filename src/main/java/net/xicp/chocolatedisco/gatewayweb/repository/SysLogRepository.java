package net.xicp.chocolatedisco.gatewayweb.repository;

import net.xicp.chocolatedisco.gatewayweb.entity.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by SunYu on 2018/3/6.
 */
public interface SysLogRepository extends JpaRepository<SysLog, Long>, JpaSpecificationExecutor<SysLog> {
}
