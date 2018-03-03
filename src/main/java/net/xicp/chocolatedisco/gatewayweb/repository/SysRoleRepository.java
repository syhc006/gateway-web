package net.xicp.chocolatedisco.gatewayweb.repository;

import net.xicp.chocolatedisco.gatewayweb.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface SysRoleRepository extends JpaRepository<SysRole, Long>, JpaSpecificationExecutor<SysRole> {

    List<SysRole> findByIdIn(List<Long> ids);
}
