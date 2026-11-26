package com.networkanomaly.backend.scheduler;

import com.networkanomaly.backend.service.TrafficService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TrafficRetentionScheduler {

    private static final Logger log = LoggerFactory.getLogger(TrafficRetentionScheduler.class);

    @Autowired
    private TrafficService trafficService;

    @Value("${traffic.retention.days:7}")
    private int retentionDays;

    // Run every day at 02:00 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void purgeOldNormalTrafficDaily() {
        log.info("Starting automated retention cleanup for normal traffic older than {} days...", retentionDays);
        int deleted = trafficService.purgeNormalTrafficOlderThanDays(retentionDays);
        log.info("Retention cleanup completed. Deleted {} old normal traffic records. Anomaly records were preserved permanently.", deleted);
    }
}
