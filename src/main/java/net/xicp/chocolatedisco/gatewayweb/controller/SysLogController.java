package net.xicp.chocolatedisco.gatewayweb.controller;

import net.xicp.chocolatedisco.gatewayweb.entity.SysLog;
import net.xicp.chocolatedisco.gatewayweb.repository.SysLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by SunYu on 2018/3/6.
 */
@RestController
@RequestMapping("/gateway")
public class SysLogController extends BaseController {
    @Autowired
    private SysLogRepository sysLogRepository;

    @GetMapping("/sysLogs/{id}")
    public SysLog getSysLogById(@PathVariable(name = "id") Long id) {
        return sysLogRepository.findById(id).get();
    }

    @GetMapping("/sysLogs")
    public Page<SysLog> searchPagedSysLogsByType(SysLog condition, Integer pageNum, Integer pageSize, Long startTime, Long endTime) {
        Optional<SysLog> optionalCondition = Optional.ofNullable(condition);
        Optional<Integer> optionalPageNum = Optional.ofNullable(pageNum);
        Optional<Integer> optionalPageSize = Optional.ofNullable(pageSize);
        Optional<Long> optionalStartTime = Optional.ofNullable(startTime);
        Optional<Long> optionalEndTime = Optional.ofNullable(endTime);
        PageRequest pageRequest = PageRequest.of(optionalPageNum.orElse(0), optionalPageSize.orElse(Integer.MAX_VALUE));
        Specification<SysLog> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            optionalCondition.map(oc -> oc.getAccount()).ifPresent(account -> list.add(cb.like(root.get("account").as(String.class), "%" + account + "%")));
            optionalCondition.map(oc -> oc.getEvent()).ifPresent(event -> list.add(cb.like(root.get("event").as(String.class), "%" + event + "%")));
            optionalCondition.map(oc -> oc.getIp()).ifPresent(ip -> list.add(cb.like(root.get("ip").as(String.class), "%" + ip + "%")));
            optionalCondition.map(oc -> oc.getResult()).ifPresent(result -> list.add(cb.like(root.get("result").as(String.class), "%" + result + "%")));
            optionalStartTime.ifPresent(st -> list.add(cb.greaterThanOrEqualTo(root.get("generateTime").as(Long.class), st)));
            optionalEndTime.ifPresent(et -> list.add(cb.lessThanOrEqualTo(root.get("generateTime").as(Long.class), et)));
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        };
        return sysLogRepository.findAll(specification, pageRequest);
    }
}
