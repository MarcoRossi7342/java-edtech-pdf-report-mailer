package com.example.report;

import java.time.LocalDate;

public final class ReportPolicyTest {
    public static void main(String[] args) {
        CourseDelivery late = new CourseDelivery("c-1", "Accounting", "learner@example.com",
                LocalDate.of(2026, 10, 20), 2, 5);
        CourseDelivery complete = new CourseDelivery("c-2", "Finance", "learner@example.com",
                LocalDate.of(2026, 10, 20), 5, 5);
        ReportPolicy policy = new ReportPolicy();
        require(policy.decide(late, LocalDate.of(2026, 10, 23)).send(), "late incomplete course sends");
        require(!policy.decide(complete, LocalDate.of(2026, 10, 23)).send(), "complete course is held");
        System.out.println("ReportPolicyTest passed");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
