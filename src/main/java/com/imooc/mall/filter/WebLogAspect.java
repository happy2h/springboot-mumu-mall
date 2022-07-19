package com.imooc.mall.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 打印请求和相应信息
 */

@Aspect // 标记当前类为一个切面供容器读取
@Component
public class WebLogAspect {
    // 生成logger对象
    private final Logger log = LoggerFactory.getLogger(WebLogAspect.class);

    // 连接点，即定义在应用程序流程的何处插入切面的执行
    @Pointcut("execution(public * com.imooc.mall.controller.*.*(..))")
    public void webLog(){
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint){
        // 收到请求记录请求信息，joinPoint记录关于类的信息，方法的信息
        // 通过RequestContextHolder对象拿到请求属性
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 在属性中拿到请求信息
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        log.info("URL : " + request.getRequestURL().toString());
        log.info("HTTP_METHOD : " + request.getMethod());
        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "res", pointcut = "webLog()")
    public void doAfter(Object res) throws JsonProcessingException {
        // 处理完请求，返回内容
        log.info("RESPONSE : " + new ObjectMapper().writeValueAsString(res));
    }
}
