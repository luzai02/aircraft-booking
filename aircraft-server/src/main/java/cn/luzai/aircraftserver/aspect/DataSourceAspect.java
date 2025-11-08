package cn.luzai.aircraftserver.aspect;

import cn.luzai.aircraftserver.ddc.DataSourceContextHolder;
import cn.luzai.aircraftserver.annotation.TargetDataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据源切换 AOP
 */
@Slf4j
@Aspect
@Component
public class DataSourceAspect {

    @Around("@annotation(cn.luzai.aircraftserver.annotation.TargetDataSource)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解配置的数据源
        TargetDataSource targetDataSource = method.getAnnotation(TargetDataSource.class);
        String dataSourceName = targetDataSource.value();

        try {
            // 切换数据源
            DataSourceContextHolder.setDataSource(dataSourceName);
            log.debug("切换到数据源: {}", dataSourceName);

            // 执行目标方法
            return joinPoint.proceed();
        } finally {
            // 清除数据源上下文
            DataSourceContextHolder.clearDataSource();
            log.debug("清除数据源上下文");
        }
    }
}
