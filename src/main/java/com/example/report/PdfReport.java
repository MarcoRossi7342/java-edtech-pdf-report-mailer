package com.example.report;

import java.nio.charset.StandardCharsets;

public final class PdfReport {
    public byte[] render(CourseDelivery delivery, ReportDecision decision) {
        String text = "Course delivery report\\n" +
                "Course: " + delivery.courseName() + " (" + delivery.courseId() + ")\\n" +
                "Learner: " + delivery.learnerEmail() + "\\n" +
                "Deadline: " + delivery.deadline() + "\\n" +
                "Progress: " + delivery.completedLessons() + "/" + delivery.totalLessons() + "\\n" +
                "Decision: " + decision.reason();
        return ("%PDF-1.4\\n% report\\n" + text + "\\n%%EOF\\n").getBytes(StandardCharsets.US_ASCII);
    }
}
