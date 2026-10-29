package com.example.report;

import java.time.LocalDate;

public record CourseDelivery(String courseId, String courseName, String learnerEmail,
                             LocalDate deadline, int completedLessons, int totalLessons) {
    public boolean overdueOn(LocalDate date) {
        return date.isAfter(deadline) && completedLessons < totalLessons;
    }
}
