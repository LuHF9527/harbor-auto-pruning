package com.faw.harbor.config;

import com.faw.harbor.jobs.HarborPrunningJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Ron
 * @create 2021-1-8 13:26
 */
@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail jobDetail1(){
        return JobBuilder.newJob(HarborPrunningJob.class).storeDurably().build();
    }

    @Bean
    public Trigger trigger1(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInHours(24) //每天执行一次
                .repeatForever(); //永久重复，一直执行下去
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail1())
                .withSchedule(scheduleBuilder)
                .build();
    }
}
