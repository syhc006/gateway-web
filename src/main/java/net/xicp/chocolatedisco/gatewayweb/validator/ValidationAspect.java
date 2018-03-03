package net.xicp.chocolatedisco.gatewayweb.validator;

import net.xicp.chocolatedisco.gatewayweb.exception.ErrorInformationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@Aspect
public class ValidationAspect {
    @Pointcut(value = "execution(* net.xicp.chocolatedisco.logtransformerweb.controller..add*(..))" +
            "||execution(* net.xicp.chocolatedisco.logtransformerweb.controller..edit*(..))")
    public void validationPointcut() {
    }

    // 切入点表达式 在参数中有BindingResult的,(添加，修改 ，查询 方法)
    @Around(value = "validationPointcut()")
    public Object customAround(ProceedingJoinPoint joinPoint) throws Throwable {
        BindingResult bindingResult = null;
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            for (Object object : args) {
                if (object instanceof BindingResult) {
                    bindingResult = (BindingResult) object;
                    break;
                }
            }
        }
        if (bindingResult != null && bindingResult.hasErrors()) {
            throw new ErrorInformationException(bindingResult.getFieldError().getDefaultMessage());
        }
        return joinPoint.proceed();
    }
}
