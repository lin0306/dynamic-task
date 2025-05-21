package com.lin.dynamictask.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * cron表达式枚举
 *
 * @author 林维家
 * @since 2025/5/21 11:30
 */
@Getter
@AllArgsConstructor
public enum CronExpressionEnum {
    /**
     * 每年执行一次
     */
    yearly("@yearly", "0 0 0 1 1 *"),
    /**
     * 每年执行一次
     */
    annually("@annually", "0 0 0 1 1 *"),
    /**
     * 每月执行一次
     */
    monthly("@monthly", "0 0 0 1 * *"),
    /**
     * 每周执行一次
     */
    weekly("@weekly", "0 0 0 * * 0"),
    /**
     * 每天执行一次
     */
    daily("@daily", "0 0 0 * * *"),
    /**
     * 每天的0点执行一次
     */
    midnight("@midnight", "0 0 0 * * *"),
    /**
     * 每小时执行一次
     */
    hourly("@hourly", "0 0 * * * *");

    /**
     * 注解
     */
    final String annotation;

    /**
     * cron表达式
     */
    final String cronExpression;

    public static CronExpressionEnum getByCode(String code) {
        return Arrays.stream(CronExpressionEnum.values())
                .filter(e -> e.getAnnotation().equals(code))
                .findAny()
                .orElse(null);
    }
}
