package com.lin.dynamictask.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 任务管理
 *
 * @author 林维家
 * @since 2025/5/6 11:32
 */
@Slf4j
@Component
public class DynamicTaskConfig {

    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 存储任务Future的Map
     */
    private static final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * 添加一个新的定时任务
     *
     * @param taskId         任务id
     * @param cronExpression cron表达式
     * @param task           任务线程
     */
    public void addCronTask(String taskId, String cronExpression, Runnable task) {
        // 验证cronExpression是否有效
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }

        try {
            // 检查cron表达式的合法性
            if (!CronExpression.isValidExpression(cronExpression)) {
                throw new IllegalArgumentException("无效的cron表达式: " + cronExpression);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("cron表达式错误: " + e.getMessage(), e);
        }

        // 如果任务已存在，先移除
        if (scheduledTasks.containsKey(taskId)) {
            if (!cancelTask(taskId)) {
                log.error("任务重复创建，停止任务失败，任务ID: {}", taskId);
                // 移除失败不继续创建，避免任务数据丢失，无法停止
                return;
            }
        }

        // 创建触发器
        CronTrigger trigger = new CronTrigger(cronExpression);
        // 调度任务并保存future
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(task, trigger);
        scheduledTasks.put(taskId, future);
    }

    /**
     * 取消任务
     */
    public boolean cancelTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        if (future != null) {
            boolean cancelled = future.cancel(true);
            if (cancelled) {
                scheduledTasks.remove(taskId);
            }
            return cancelled;
        }
        return false;
    }

    /**
     * 清理所有任务
     */
    public static void clearTask() {
        if (CollectionUtils.isEmpty(scheduledTasks)) {
            return;
        }
        log.info("开始清理{}个定时任务...", scheduledTasks.size());
        scheduledTasks.values().forEach(task -> task.cancel(true));
        log.info("定时任务清理完成...");
    }
}
