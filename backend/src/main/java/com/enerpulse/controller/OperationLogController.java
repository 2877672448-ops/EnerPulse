package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.common.api.PageResult;
import com.enerpulse.entity.OperationLog;
import com.enerpulse.repository.OperationLogRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {
    private final OperationLogRepository operationLogRepository;

    @GetMapping
    public ApiResponse<PageResult<OperationLog>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startTime,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endTime) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<OperationLog> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            if (userId != null) preds.add(cb.equal(root.get("userId"), userId));
            if (module != null) preds.add(cb.equal(root.get("module"), module));
            if (action != null) preds.add(cb.equal(root.get("action"), action));
            if (startTime != null) preds.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startTime));
            if (endTime != null) preds.add(cb.lessThanOrEqualTo(root.get("createdAt"), endTime));
            return cb.and(preds.toArray(new Predicate[0]));
        };
        Page<OperationLog> p = operationLogRepository.findAll(spec, pageable);
        return ApiResponse.success(new PageResult<>(p.getContent(), page, pageSize, p.getTotalElements()));
    }
}
