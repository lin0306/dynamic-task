package com.lin.dynamictask.config;

import com.lin.dynamictask.annotation.DynamicTask;
import com.lin.dynamictask.constants.PatternConstants;
import com.lin.dynamictask.enums.CronExpressionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务初始化
 * <p>在所有 Bean 初始化完成、嵌入式容器启动后触发（完全就绪状态）</p>
 *
 * @author 林维家
 * @since 2025/5/6 11:38
 */
@Slf4j
@Component
public class DynamicTaskProcessor implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private DynamicTaskConfig dynamicTaskConfig;

    @Resource
    private ApplicationContext context;

    @Value("${dynamic.schedule.enable:false}")
    private boolean enable;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (enable) {
            // 扫描所有带 @DynamicTask 注解的方法
            Map<String, Object> beans = context.getBeansWithAnnotation(Component.class);
            AtomicInteger count = new AtomicInteger(0);
            beans.forEach((beanName, bean) -> {
                Method[] methods = ReflectionUtils.getAllDeclaredMethods(AopUtils.getTargetClass(bean));
                for (Method method : methods) {
                    DynamicTask annotation = AnnotationUtils.findAnnotation(method, DynamicTask.class);
                    // 排除“-”
                    if (annotation != null && !"-".equalsIgnoreCase(annotation.cron())) {
                        String taskId = generateTaskId(annotation, bean, method);
                        String cron = getCron(annotation, taskId);
                        if (!StringUtils.hasText(cron)) {
                            continue;
                        }
                        registerTask(bean, method, taskId, cron);
                        count.getAndIncrement();
                    }
                }
            });
            log.info("已注册{}个定时任务", count.get());
        }
    }

    private String getCron(DynamicTask annotation, String taskId) {
        String cron = annotation.cron();
        if (PatternConstants.$.matcher(cron).matches()) {
            String cronValue = cron.substring(2, cron.length() - 1);
            cron = context.getEnvironment().getProperty(cronValue);
            if (!StringUtils.hasText(cron)) {
                log.error("任务ID: {}，{} 未配置", taskId, cronValue);
            }
        } else {
            if (!CronExpression.isValidExpression(cron)) {
                log.error("任务ID: {}，cron表达式错误: {}", taskId, annotation.cron());
            }
            // 如果是注解的形式，则替换为对应的cron表达式
            if (cron.startsWith("@")) {
                CronExpressionEnum cronExpressionEnum = CronExpressionEnum.getByCode(cron);
                if (cronExpressionEnum != null) {
                    cron = cronExpressionEnum.getCronExpression();
                } else {
                    log.error("任务ID: {}，不支持的注解: {}", taskId, cron);
                }
            }
        }
        return cron;
    }

    /**
     * 服务停止时销毁所有定时任务
     */
    @PreDestroy
    public void destroy() {
        if (enable) {
            // 执行清理逻辑
            DynamicTaskConfig.clearTask();
        }
    }

    /**
     * 注册任务
     *
     * @param bean   类
     * @param method 方法
     * @param taskId 任务id
     * @param cron   定时任务表达式
     */
    private void registerTask(Object bean, Method method, String taskId, String cron) {
        Runnable task = () -> {
            try {
                method.invoke(bean);
            } catch (Exception e) {
                throw new RuntimeException("定时任务执行失败: " + taskId, e);
            }
        };

        dynamicTaskConfig.addCronTask(taskId, cron, task);
    }

    /**
     * 生成任务ID
     *
     * @param annotation 注解
     * @param bean       类信息
     * @param method     方法
     * @return 任务ID
     */
    private String generateTaskId(DynamicTask annotation, Object bean, Method method) {
        if (!StringUtils.hasText(annotation.taskId())) {
            String className = bean.getClass().getSimpleName();
            String methodName = method.getName();
            // 示例：DynamicTask#task
            return className + "#" + methodName;
        } else {
            return annotation.taskId();
        }
    }
}
