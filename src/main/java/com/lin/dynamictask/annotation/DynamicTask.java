package com.lin.dynamictask.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义定时任务注解
 *
 * @author 林维家
 * @since 2025/5/6 11:51
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicTask {

    /**
     * 任务标识，默认使用类名+#+方法名，如：DynamicTask#test
     */
    String taskId() default "";

    /**
     * cron表达式
     * <p>
     * 目前支持的表达式格式如下：
     *     <ul>
     *         <li>字符串格式：{@code "0 * * * * MON-FRI"}</li>
     *         <li>读取配置文件内容：{@code ${...}}</li>
     *         <li>使用注解：具体参考{@link com.lin.dynamictask.enums.CronExpressionEnum}</li>
     *         <li>不启用定时任务：{@code -}</li>
     *     </ul>
     * </p>
     */
    String cron();
}
