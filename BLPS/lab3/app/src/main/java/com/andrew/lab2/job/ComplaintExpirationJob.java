package com.andrew.lab2.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import com.andrew.lab2.service.ComplaintService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor 
public class ComplaintExpirationJob implements Job {
    private final ComplaintService complaintService;

    @Override
    public void execute(JobExecutionContext context) {
        Long complaintId = context.getMergedJobDataMap()
            .getLong("complaintId");

        complaintService.expireIfPending(complaintId);
    }
}
