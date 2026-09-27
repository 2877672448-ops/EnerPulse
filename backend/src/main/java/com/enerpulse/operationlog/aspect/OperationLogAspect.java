package com.enerpulse.operationlog.aspect;

import com.enerpulse.common.api.RequestIdHolder;
import com.enerpulse.repository.OperationLogRepository;
import com.enerpulse.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    private final OperationLogRepository operationLogRepository;

    @AfterReturning(pointcut = "@annotation(operationLog)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, com.enerpulse.operationlog.OperationLog operationLog, Object result) {
        try {
            com.enerpulse.entity.OperationLog logEntity = new com.enerpulse.entity.OperationLog();
            logEntity.setModule(operationLog.module());
            logEntity.setAction(operationLog.action());
            logEntity.setUserId(SecurityUtils.getCurrentUserId());
            logEntity.setRequestId(RequestIdHolder.get());

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                logEntity.setIp(req.getRemoteAddr());
                logEntity.setUserAgent(req.getHeader("User-Agent"));
            }

            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("args", args);
                logEntity.setDetail(detail);
            }
            operationLogRepository.save(logEntity);
        } catch (Exception e) {
            log.warn("记录操作日志失败", e);
        }
    }
}
