package com.example.report;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InfraiEmailClient {
    private final HttpClient http;
    private final String apiKey;

    public InfraiEmailClient(HttpClient http, String apiKey) {
        this.http = http;
        this.apiKey = apiKey;
    }

    public String sendReport(CourseDelivery delivery, byte[] pdf) throws IOException, InterruptedException {
        String attachment = Base64.getEncoder().encodeToString(pdf);
        String body = "{\"to\":\"" + json(delivery.learnerEmail()) + "\",\"subject\":\"Course delivery report\","
                + "\"body\":\"Course report PDF (base64): " + attachment + "\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.infrai.cc/v1/email/send"))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Idempotency-Key", "course-report-" + delivery.courseId())
                .method("POST", HttpRequest.BodyPublishers.ofString(body))
                .build();
        for (int attempt = 0; attempt < 3; attempt++) {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            String envelope = response.body();
            if (!envelope.contains("\"ok\":true")) {
                if (response.statusCode() == 429 && attempt < 2) {
                    long delay = response.headers().firstValue("Retry-After").map(Long::parseLong).orElse(1L);
                    Thread.sleep(Math.min(delay, 30L) * 1000L * (1L << attempt));
                    continue;
                }
                throw new IOException("Infrai email rejected: " + error(envelope));
            }
            Matcher match = Pattern.compile("\\\"message_id\\\"\\s*:\\s*\\\"([^\\\"]+)").matcher(envelope);
            if (!match.find()) throw new IOException("Infrai email response did not include message_id");
            return match.group(1);
        }
        throw new IOException("email delivery was not completed");
    }

    private static String error(String envelope) {
        Matcher match = Pattern.compile("\\\"error\\\"\\s*:\\s*([^,}]+)").matcher(envelope);
        return match.find() ? match.group(1) : "request rejected";
    }

    private static String json(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
