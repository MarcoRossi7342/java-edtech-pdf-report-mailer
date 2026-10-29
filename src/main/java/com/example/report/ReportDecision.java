package com.example.report;

public record ReportDecision(boolean send, String reason) {
    public static ReportDecision send(String reason) { return new ReportDecision(true, reason); }
    public static ReportDecision hold(String reason) { return new ReportDecision(false, reason); }
}
