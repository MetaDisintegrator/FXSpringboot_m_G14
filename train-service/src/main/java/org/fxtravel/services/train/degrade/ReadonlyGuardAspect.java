package org.fxtravel.services.train.degrade;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReadonlyGuardAspect {

    private final ServiceDegradeConfig cfg;

    public ReadonlyGuardAspect(ServiceDegradeConfig cfg) { this.cfg = cfg; }

    // 拦截所有“写”方法（Controller 上的 POST/PUT/PATCH/DELETE）
    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) "  +
            "|| @annotation(org.springframework.web.bind.annotation.PatchMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object blockWrites(ProceedingJoinPoint pjp) throws Throwable {
        var mode = cfg.mode();
        if (mode == ServiceDegradeConfig.Mode.disabled) {
            throw new ServiceDisabledException("Service disabled by degrade");
        }
        if (mode == ServiceDegradeConfig.Mode.readonly) {
            throw new ReadonlyException("Service readonly by degrade");
        }
        return pjp.proceed();
    }

    /** 只读异常（423 LOCKED） */
    public static class ReadonlyException extends RuntimeException {
        public ReadonlyException(String m){ super(m); }
    }

    /** 关停异常（503 SERVICE_UNAVAILABLE） */
    public static class ServiceDisabledException extends RuntimeException {
        public ServiceDisabledException(String m){ super(m); }
    }
}
