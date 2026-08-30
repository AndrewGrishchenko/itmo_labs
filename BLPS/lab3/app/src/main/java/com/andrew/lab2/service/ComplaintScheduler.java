package com.andrew.lab2.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

import com.andrew.lab2.job.ComplaintExpirationJob;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class ComplaintScheduler {
    private final Scheduler scheduler;

    public void scheduleExpiration(Long complaintId) {
        JobDetail jobDetail = JobBuilder
            .newJob(ComplaintExpirationJob.class)
            .withIdentity(
                "complaint-expiration-" + complaintId
            )
            .usingJobData("complaintId", complaintId)
            .build();

        Trigger trigger = TriggerBuilder
            .newTrigger()
            .withIdentity(
                "complaint-expiration-trigger-" + complaintId
            )
            .startAt(
                Date.from(
                    // Instant.now().plus(48, ChronoUnit.HOURS)
                    Instant.now().plus(1, ChronoUnit.MINUTES)
                )
            )
            .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("scheduler exception", e);
        }
    }
}
