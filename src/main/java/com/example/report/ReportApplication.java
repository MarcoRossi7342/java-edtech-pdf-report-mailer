package com.example.report;

import java.net.http.HttpClient;
import java.time.LocalDate;

public final class ReportApplication {
    public static void main(String[] args) throws Exception {
        String key = System.getenv("INFRAI_API_KEY");
        String recipient = System.getenv("REPORT_EMAIL_TO");
        if (key == null || key.isBlank() || recipient == null || recipient.isBlank()) {
            throw new IllegalStateException("INFRAI_API_KEY and REPORT_EMAIL_TO are required");
        }
        CourseDelivery delivery = new CourseDelivery("course-101", "Applied Statistics", recipient,
                LocalDate.now().minusDays(1), 7, 10);
        ReportService service = new ReportService(new ReportPolicy(), new PdfReport(),
                new InfraiEmailClient(HttpClient.newHttpClient(), key));
        ReportDecision result = service.emailIfOverdue(delivery, LocalDate.now());
        System.out.println(result.send() ? "report sent" : "report held: " + result.reason());
    }
}
