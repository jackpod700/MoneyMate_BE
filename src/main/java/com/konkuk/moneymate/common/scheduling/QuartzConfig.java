package com.konkuk.moneymate.common.scheduling;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail stockFetchJobDetail() {
        return JobBuilder.newJob(StockLastdayFetchJob.class)
                .withIdentity("stockLastdayFetchJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger stockFetchTrigger(JobDetail stockFetchJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(stockFetchJobDetail)
                .withIdentity("stockLastdayFetchTrigger")
                .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.HOUR))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(3)
                        .repeatForever())
                .build();
    }
}
