package cn.luzai.aircraftserver.annotation;

import java.lang.annotation.*;

/**
 * 目标数据源注解
 * 用法：@TargetDataSource("airline-a")
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    /**
     * 数据源名称（meta/airline-a/airline-b/airline-c）
     */
    String value() default "meta";
}
