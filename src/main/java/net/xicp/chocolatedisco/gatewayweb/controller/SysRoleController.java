package net.xicp.chocolatedisco.gatewayweb.controller;

import net.xicp.chocolatedisco.gatewayweb.entity.SysRole;
import net.xicp.chocolatedisco.gatewayweb.exception.DuplicateInformationException;
import net.xicp.chocolatedisco.gatewayweb.repository.SysRoleRepository;
import net.xicp.chocolatedisco.gatewayweb.validator.group.AddValidationGroup;
import net.xicp.chocolatedisco.gatewayweb.validator.group.EditValidationGroup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gateway")
public class SysRoleController extends BaseController {
    @Autowired
    private SysRoleRepository sysRoleRepository;

    @GetMapping("/sysRoles/{id}")

    public SysRole getSysRoleById(@PathVariable(name = "id") Long id) {
        return sysRoleRepository.findById(id).get();
    }

    @GetMapping("/sysRoles")

    public Page<SysRole> searchPagedSysRolesByType(SysRole condition, Integer pageNum, Integer pageSize) {
        Optional<SysRole> optionalCondition = Optional.ofNullable(condition);
        Optional<Integer> optionalPageNum = Optional.ofNullable(pageNum);
        Optional<Integer> optionalPageSize = Optional.ofNullable(pageSize);
        PageRequest pageRequest = PageRequest.of(optionalPageNum.orElse(0), optionalPageSize.orElse(Integer.MAX_VALUE));
        Specification<SysRole> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            optionalCondition.map(oc -> oc.getDisplayName()).ifPresent(displayName -> list.add(cb.like(root.get("displayName").as(String.class), "%" + displayName + "%")));
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        };
        return sysRoleRepository.findAll(specification, pageRequest);
    }

    @PostMapping("/sysRoles")

    @Transactional
    public SysRole addSysRole(@RequestBody @Validated(value = {AddValidationGroup.class}) SysRole sysRole, BindingResult result) throws Exception {
        boolean isDuplication = sysRoleRepository.findAll().stream().anyMatch(
                sysRole1 -> sysRole1.getDisplayName().equals(sysRole.getDisplayName())
        );
        if (isDuplication) {
            throw new DuplicateInformationException("系统角色\"" + sysRole.getDisplayName() + "\"已存在");
        }
        SysRole sysRoleInDb = sysRoleRepository.saveAndFlush(sysRole);
        return sysRoleInDb;
    }

    @PutMapping("/sysRoles/{id}")

    @Transactional
    public SysRole editSysRoleById(@PathVariable Long id, @RequestBody @Validated(value = {EditValidationGroup.class}) SysRole sysRole, BindingResult result, @RequestParam String flag) throws Exception {
        SysRole sysRoleInDb = sysRoleRepository.findById(id).get();
        if (flag.equals("roleEdit")) {
            boolean isDuplication = sysRoleRepository.findAll().stream().anyMatch(
                    sysRole1 -> sysRole1.getId() == id ? false :
                            sysRole1.getDisplayName().equals(sysRole.getDisplayName())
            );
            if (isDuplication) {
                throw new DuplicateInformationException("系统角色\"" + sysRole.getDisplayName() + "\"已存在");
            }
            BeanUtils.copyProperties(sysRole, sysRoleInDb, "id", "sysResources");
        }
        if (flag.equals("resourceClean") || flag.equals("resourceBind")) {
            BeanUtils.copyProperties(sysRole, sysRoleInDb, "id", "displayName");
        }
        sysRoleRepository.save(sysRoleInDb);
        return sysRoleInDb;
    }

    @DeleteMapping("/sysRoles/{ids}")

    @Transactional
    public List<Long> removeSysRoleById(@PathVariable List<Long> ids) throws Exception {
        List<SysRole> sysRoles = sysRoleRepository.findByIdIn(ids);
        for (SysRole sysRole : sysRoles) {
            if (sysRole.getSysUsers().size() != 0) {
                throw new Exception("角色已绑定用户，请先解除此角色的绑定关系");
            }
        }
        sysRoleRepository.deleteInBatch(sysRoles);
        return ids;
    }

}
