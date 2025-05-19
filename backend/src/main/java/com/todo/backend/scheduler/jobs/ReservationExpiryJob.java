package com.todo.backend.scheduler.jobs;

import com.todo.backend.service.ReservationService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpiryJob implements Job {
    private ReservationService reservationService;

    @Autowired
    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String reservationId = jobDataMap.getString("reservationId");
        int retryCount = jobDataMap.getInt("retryCount");

        try {
            reservationService.setExpiredReservation(reservationId);
        }
        catch (Exception e) {
            if (retryCount < 3) {
                jobDataMap.put("retryCount", retryCount + 1);
                throw new JobExecutionException("Retrying to set reservation as expired: " + reservationId, e, true);
            }

            // TODO: Throw error while developing, change to log error in production
            throw new JobExecutionException("Retrying to set reservation as expired: " + reservationId, e, false);
        }
    }
}
