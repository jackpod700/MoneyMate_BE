package com.konkuk.moneymate.common.scheduling;


import com.konkuk.moneymate.activities.stock.service.StockLastdayFetchService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockLastdayFetchJob implements Job {

    private final StockLastdayFetchService stockLastdayFetchService;

    @Override
    public void execute(JobExecutionContext context) {
        stockLastdayFetchService.fetchLastdayPrices();
    }
}
