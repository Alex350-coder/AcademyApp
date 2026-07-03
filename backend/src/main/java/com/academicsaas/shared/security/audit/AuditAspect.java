package com.academicsaas.shared.security.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT_LOG");

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @Pointcut("execution(* com.academicsaas..application.usecase.RegisterUserByDirectorUseCase.execute(..)) || " +
              "execution(* com.academicsaas..application.usecase.DeactivateUserUseCase.execute(..)) || " +
              "execution(* com.academicsaas..application.usecase.EnrollStudentUseCase.execute(..))")
    public void sensitiveOperations() {}

    @Pointcut("execution(* com.academicsaas..presentation.controller.*.delete*(..))")
    public void deleteOperations() {}

    @AfterReturning("sensitiveOperations()")
    public void auditSensitiveOperation(JoinPoint joinPoint) {
        var method = joinPoint.getSignature().getName();
        var target = joinPoint.getTarget().getClass().getSimpleName();
        var args = joinPoint.getArgs();
        auditLog.info("AUDIT: {}.{} called with args: {}",
            target, method, maskArguments(args));
    }

    @AfterReturning("deleteOperations()")
    public void auditDeleteOperation(JoinPoint joinPoint) {
        var method = joinPoint.getSignature().getName();
        var target = joinPoint.getTarget().getClass().getSimpleName();
        auditLog.warn("AUDIT-DELETE: {}.{} executed", target, method);
    }

    private String maskArguments(Object[] args) {
        if (args == null) {
            return "[]";
        }
        var sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            var arg = args[i];
            if (arg == null) {
                sb.append("null");
                continue;
            }
            var str = arg.toString();
            if (str.length() > 100) {
                str = str.substring(0, 100) + "...";
            }
            sb.append(str);
        }
        sb.append("]");
        return sb.toString();
    }
}
