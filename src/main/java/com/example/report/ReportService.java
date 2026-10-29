package com.example.report;

import java.time.LocalDate;

public final class ReportService {
    private final ReportPolicy policy;
    private final PdfReport pdfReport;
    private final InfraiEmailClient emailClient;

    public ReportService(ReportPolicy policy, PdfReport pdfReport, InfraiEmailClient emailClient) {
        this.policy = policy;
        this.pdfReport = pdfReport;
        this.emailClient = emailClient;
    }

    public ReportDecision emailIfOverdue(CourseDelivery delivery, LocalDate today) throws Exception {
        ReportDecision decision = policy.decide(delivery, today);
        if (decision.send()) emailClient.sendReport(delivery, pdfReport.render(delivery, decision));
        return decision;
    }
}
