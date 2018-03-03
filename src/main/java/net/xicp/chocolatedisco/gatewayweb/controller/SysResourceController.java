package net.xicp.chocolatedisco.gatewayweb.controller;

import com.alibaba.fastjson.JSONObject;
import net.xicp.chocolatedisco.gatewayweb.entity.SysResource;
import net.xicp.chocolatedisco.gatewayweb.exception.DuplicateInformationException;
import net.xicp.chocolatedisco.gatewayweb.exception.MissingInformationException;
import net.xicp.chocolatedisco.gatewayweb.repository.SysResourceRepository;
import net.xicp.chocolatedisco.gatewayweb.validator.group.AddValidationGroup;
import net.xicp.chocolatedisco.gatewayweb.validator.group.EditValidationGroup;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gateway")
public class SysResourceController extends BaseController {
    @Autowired
    private SysResourceRepository sysResourceRepository;

    @GetMapping("/sysResources/{id}")

    public SysResource getSysResourceById(@PathVariable(name = "id") Long id) {
        return sysResourceRepository.findById(id).get();
    }

    @GetMapping("/sysResources")

    public Page<SysResource> searchPagedSysResourcesByType(SysResource condition, Integer pageNum, Integer pageSize) {
        Optional<SysResource> optionalCondition = Optional.ofNullable(condition);
        Optional<Integer> optionalPageNum = Optional.ofNullable(pageNum);
        Optional<Integer> optionalPageSize = Optional.ofNullable(pageSize);
        PageRequest pageRequest = PageRequest.of(optionalPageNum.orElse(0), optionalPageSize.orElse(Integer.MAX_VALUE));
        Specification<SysResource> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            optionalCondition.map(oc -> oc.getDisplayName()).ifPresent(displayName -> list.add(cb.like(root.get("displayName").as(String.class), "%" + displayName + "%")));
            optionalCondition.map(oc -> oc.getClassification()).ifPresent(classification -> list.add(cb.like(root.get("classification").as(String.class), "%" + classification + "%")));
            optionalCondition.map(oc -> oc.getCode()).ifPresent(code -> list.add(cb.like(root.get("code").as(String.class), "%" + code + "%")));
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        };
        return sysResourceRepository.findAll(specification, pageRequest);
    }

    @PostMapping("/sysResources")

    @Transactional
    public SysResource addSysResource(@RequestBody @Validated(value = {AddValidationGroup.class}) SysResource sysResource, BindingResult result) throws Exception {
        boolean isDuplication = sysResourceRepository.findAll().stream().anyMatch(
                sysResource1 -> sysResource1.getDisplayName().equals(sysResource.getDisplayName()) || sysResource1.getCode().equals(sysResource.getCode())
        );
        if (isDuplication) {
            throw new DuplicateInformationException("系统资源\"" + sysResource.getDisplayName() + "\"已存在或资源标识\"" + sysResource.getCode() + "\"已存在");
        }
        SysResource sysResourceInDb = sysResourceRepository.saveAndFlush(sysResource);
        return sysResourceInDb;
    }

    @PutMapping("/sysResources/{id}")

    @Transactional
    public SysResource editSysResourceById(@PathVariable Long id, @RequestBody @Validated(value = {EditValidationGroup.class}) SysResource sysResource, BindingResult result) throws Exception {
        boolean isDuplication = sysResourceRepository.findAll().stream().anyMatch(
                sysResource1 -> sysResource1.getId() == id ? false :
                        sysResource1.getDisplayName().equals(sysResource.getDisplayName()) || sysResource1.getCode().equals(sysResource.getCode())
        );
        if (isDuplication) {
            throw new DuplicateInformationException("系统资源\"" + sysResource.getDisplayName() + "\"已存在或资源标识\"" + sysResource.getCode() + "\"已存在");
        }
        SysResource sysResourceInDb = sysResourceRepository.findById(id).orElseThrow(() -> new MissingInformationException("无此资源"));
        copyNonNullProperties(sysResource, sysResourceInDb, "id");
        sysResourceRepository.save(sysResourceInDb);
        return sysResourceInDb;
    }

    @DeleteMapping("/sysResources/{ids}")

    @Transactional
    public List<Long> removeSysResourceById(@PathVariable List<Long> ids) throws Exception {
        List<SysResource> sysResources = sysResourceRepository.findByIdIn(ids);
        sysResourceRepository.deleteInBatch(sysResources);
        return ids;
    }
}
