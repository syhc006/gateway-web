package net.xicp.chocolatedisco.gatewayweb.controller;

import net.xicp.chocolatedisco.gatewayweb.entity.SysUser;
import net.xicp.chocolatedisco.gatewayweb.exception.DuplicateInformationException;
import net.xicp.chocolatedisco.gatewayweb.repository.SysUserRepository;
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
public class SysUserController extends BaseController {
    @Autowired
    private SysUserRepository sysUserRepository;

    @GetMapping("/sysUsers/{id}")

    public SysUser getSysUserById(@PathVariable(name = "id") Long id) {
        return sysUserRepository.findById(id).get();
    }

    @GetMapping("/sysUsers")

    public Page<SysUser> searchPagedSysUsersByType(SysUser condition, Integer pageNum, Integer pageSize) {
        Optional<SysUser> optionalCondition = Optional.ofNullable(condition);
        Optional<Integer> optionalPageNum = Optional.ofNullable(pageNum);
        Optional<Integer> optionalPageSize = Optional.ofNullable(pageSize);
        PageRequest pageRequest = PageRequest.of(optionalPageNum.orElse(0), optionalPageSize.orElse(Integer.MAX_VALUE));
        Specification<SysUser> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            optionalCondition.map(oc -> oc.getAccount()).ifPresent(account -> list.add(cb.like(root.get("account").as(String.class), "%" + account + "%")));
            optionalCondition.map(oc -> oc.getSysRole()).map(sysRole -> sysRole.getId()).ifPresent(id -> list.add(cb.equal(root.get("sysRole").get("id").as(Long.class), id)));
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        };
        return sysUserRepository.findAll(specification, pageRequest);
    }

    @PostMapping("/sysUsers")

    @Transactional
    public SysUser addSysUser(@RequestBody @Validated(value = {AddValidationGroup.class}) SysUser sysUser, BindingResult result) throws Exception {
        boolean isDuplication = sysUserRepository.findAll().stream().anyMatch(
                sysUser1 -> sysUser1.getAccount().equals(sysUser.getAccount())
        );
        if (isDuplication) {
            throw new DuplicateInformationException("系统用户\"" + sysUser.getAccount() + "\"已存在");
        }
        SysUser sysUserInDb = sysUserRepository.saveAndFlush(sysUser);
        return sysUserInDb;
    }

    @PutMapping("/sysUsers/{id}")

    @Transactional
    public SysUser editSysUserById(@PathVariable Long id, @RequestBody @Validated(value = {EditValidationGroup.class}) SysUser sysUser, BindingResult result, @RequestParam String flag) throws Exception {
        SysUser sysUserInDb = sysUserRepository.findById(id).get();
        if (flag.equals("userEdit")) {
            boolean isDuplication = sysUserRepository.findAll().stream().anyMatch(
                    sysUser1 -> sysUser1.getId() == id ? false :
                            sysUser1.getAccount().equals(sysUser.getAccount())
            );
            if (isDuplication) {
                throw new DuplicateInformationException("系统用户\"" + sysUser.getAccount() + "\"已存在");
            }
            BeanUtils.copyProperties(sysUser, sysUserInDb, "id", "sysRole");
        }
        if (flag.equals("roleClean") || flag.equals("roleBind")) {
            BeanUtils.copyProperties(sysUser, sysUserInDb, "id", "account", "password");
        }
        sysUserRepository.save(sysUserInDb);
        return sysUserInDb;
    }

    @DeleteMapping("/sysUsers/{ids}")

    @Transactional
    public List<Long> removeSysUserById(@PathVariable List<Long> ids) throws Exception {
        List<SysUser> sysUsers = sysUserRepository.findByIdIn(ids);
        sysUserRepository.deleteInBatch(sysUsers);
        return ids;
    }
}
