package net.xicp.chocolatedisco.gatewayweb.repository;

import net.xicp.chocolatedisco.gatewayweb.entity.SysResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface SysResourceRepository extends JpaRepository<SysResource, Long>, JpaSpecificationExecutor<SysResource> {

    List<SysResource> findByIdIn(List<Long> ids);

    SysResource findByCode(String code);
}
