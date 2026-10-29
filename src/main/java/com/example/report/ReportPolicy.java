package com.example.report;

import java.time.LocalDate;

public final class ReportPolicy {
    public ReportDecision decide(CourseDelivery delivery, LocalDate today) {
        if (delivery.learnerEmail() == null || delivery.learnerEmail().isBlank()) {
            return ReportDecision.hold("learner email is required");
        }
        if (!delivery.overdueOn(today)) {
            return ReportDecision.hold("deadline has not passed or the course is complete");
        }
        return ReportDecision.send("deadline passed with lessons outstanding");
    }
}
