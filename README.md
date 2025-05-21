# Dynamic Task Scheduling Framework

基于Spring Boot的动态定时任务调度框架，支持通过注解配置任务和动态管理。该框架提供了简单易用的方式来创建和管理定时任务，无需编写复杂的配置代码。

## 功能特点

- 通过注解方式快速定义定时任务
- 支持多种Cron表达式格式（标准格式、预定义注解、配置文件引用）
- 动态管理定时任务（添加、取消、清理）
- 自动任务ID生成
- 线程池调度执行
- 优雅关闭（应用停止时自动清理任务）

## 技术栈

- Java 1.8
- Spring Boot 2.7.18

## 快速开始

[//]: # (### 添加依赖)

[//]: # ()
[//]: # (在你的Maven项目中添加以下依赖：)

[//]: # ()
[//]: # (```xml)

[//]: # (<dependency>)

[//]: # (    <groupId>com.lin</groupId>)

[//]: # (    <artifactId>dynamic-task</artifactId>)

[//]: # (    <version>0.0.1-SNAPSHOT</version>)

[//]: # (</dependency>)

[//]: # (```)

### 启用定时任务

在`application.properties`或`application.yml`中添加以下配置启用定时任务：

```properties
# 启用动态定时任务
dynamic.schedule.enable=true
```

## 使用方式

### 1. 添加定时任务注解

在Spring管理的Bean方法上添加`@DynamicTask`注解：

```java
@Component
public class TaskDemo {

    // 使用标准Cron表达式（每分钟执行一次）
    @DynamicTask(cron = "0 * * * * *")
    public void standardCronTask() {
        System.out.println("标准Cron表达式任务执行：" + LocalDateTime.now());
    }
    
    // 使用预定义注解（每天执行一次）
    @DynamicTask(cron = "@daily")
    public void annotationTask() {
        System.out.println("预定义注解任务执行：" + LocalDateTime.now());
    }
    
    // 从配置文件读取Cron表达式
    @DynamicTask(cron = "${task.cron.expression}")
    public void configTask() {
        System.out.println("配置文件任务执行：" + LocalDateTime.now());
    }
    
    // 自定义任务ID
    @DynamicTask(taskId = "customTaskId", cron = "0 0 12 * * ?")
    public void customIdTask() {
        System.out.println("自定义ID任务执行：" + LocalDateTime.now());
    }
}
```

### 2. 配置文件中定义Cron表达式

当使用`${...}`格式引用配置时，需要在配置文件中定义对应的属性：

```properties
# 定时任务Cron表达式
task.cron.expression=0 0/30 * * * ?
```

## Cron表达式支持

框架支持以下几种Cron表达式格式：

### 标准Cron表达式

使用标准的Spring Cron表达式格式，例如：`0 0 12 * * ?`（每天中午12点执行）

### 预定义注解

框架提供了以下预定义的Cron表达式注解：

| 注解 | 描述 | Cron表达式 |
|------|------|------------|
| `@yearly` 或 `@annually` | 每年执行一次 | `0 0 0 1 1 *` |
| `@monthly` | 每月执行一次 | `0 0 0 1 * *` |
| `@weekly` | 每周执行一次 | `0 0 0 * * 0` |
| `@daily` 或 `@midnight` | 每天执行一次 | `0 0 0 * * *` |
| `@hourly` | 每小时执行一次 | `0 0 * * * *` |

### 配置文件引用

使用`${property.name}`格式从配置文件中读取Cron表达式。

### 禁用任务

使用`-`符号可以禁用定时任务。

## API文档

### @DynamicTask注解

| 属性 | 类型 | 描述 | 默认值 |
|------|------|------|--------|
| `taskId` | String | 任务标识 | 类名+#+方法名（如：DynamicTask#test） |
| `cron` | String | Cron表达式 | 无（必填） |

### DynamicTaskConfig类

框架提供了`DynamicTaskConfig`类用于动态管理定时任务：

```java
@Resource
private DynamicTaskConfig dynamicTaskConfig;

// 添加定时任务
dynamicTaskConfig.addCronTask("taskId", "0 * * * * *", () -> {
    // 任务逻辑
});

// 取消定时任务
dynamicTaskConfig.cancelTask("taskId");
```

## 配置选项

| 配置项 | 类型 | 描述 | 默认值 |
|--------|------|------|--------|
| `dynamic.schedule.enable` | Boolean | 是否启用动态定时任务 | false |

## 注意事项

1. 确保Spring容器管理的Bean上使用`@DynamicTask`注解
2. Cron表达式必须符合Spring Cron表达式格式
3. 从配置文件读取的属性必须存在，否则任务将不会被注册
4. 任务ID在系统中必须唯一

## 许可证

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证。

Apache License 2.0 是一个宽松的、允许商业使用的开源许可证，主要要求：

- 保留原始版权声明和免责声明
- 在修改版本中声明所做的更改
- 在重新分发的软件中包含许可证副本
- 如果包含NOTICE文件，则需在分发版本中包含该文件

完整的许可证文本请参阅 [Apache License 2.0 官方网站](https://www.apache.org/licenses/LICENSE-2.0)。

## 参考资料

本项目在开发过程中参考了以下资料：

- [Spring Boot 定时任务全攻略：从@Scheduled 到分布式调度，一文搞定！](https://segmentfault.com/a/1190000046470324#item-3) - 该文章详细介绍了Spring Boot中实现定时任务的多种方式，包括@Scheduled注解、Spring Task动态管理任务、Quartz和XXL-Job等方案，为本项目提供了重要的技术参考。

## 贡献

[添加贡献指南]

## 作者

林维家