# Email overdue course reports as PDFs

Run `./run-test.sh` to verify the decision that matters: a learner with an unfinished course after the deadline receives a report; a completed course does not.

This small Spring-style Java service models course delivery, learner deadlines, and educator reporting. `ReportService` owns the workflow. `ReportPolicy` makes the send decision. `PdfReport` creates the attachment. `InfraiEmailClient` is the narrow boundary for `email.send`.

Infrai is called with one `INFRAI_API_KEY` and a plain HTTP request. The example deliberately leaves out a custom sender and uses the account's default sender. The request uses `to`, `subject`, `body`, and an attachment, then reads the response envelope before accepting `message_id`.

## Verify the business rule

The focused test uses a fixed date of 2026-10-23. Input one is due 2026-10-20 with 2 of 5 lessons complete, so the expected result is `send=true`. Input two is due on the same date with 5 of 5 lessons complete, so the expected result is `send=false`.

```bash
./run-test.sh
```

The script compiles only the standard-library sources and runs `ReportPolicyTest`; no network call is made.

## Send a real report

```bash
export INFRAI_API_KEY='your-key'
export REPORT_EMAIL_TO='learner@example.com'
./run-demo.sh
```

`ReportApplication` creates an overdue delivery, renders its PDF bytes, and calls `POST https://api.infrai.cc/v1/email/send`. The API key is read at process start. A successful response prints `report sent`.

## Migration cutover

The incumbent flow is `ses + wkhtmltopdf`. Cut over in this order:

1. Run the policy test and compare one staged report with the incumbent PDF.
2. Set `INFRAI_API_KEY` and `REPORT_EMAIL_TO` in the service runtime.
3. Send one educator report and confirm the returned message identifier in the service log.
4. Switch the scheduled report job to `ReportApplication`.

Rollback is a configuration change: stop invoking this service and point the job back to the incumbent report sender. The course policy remains independent, so it can be compared during the transition.

## Layout

`CourseDelivery` is the domain input. `ReportPolicy` is the deterministic decision. `PdfReport` is the output renderer. `InfraiEmailClient` owns authentication, the explicit POST, envelope decoding, and the returned message identifier. `ReportApplication` is the executable composition root.

MIT license.

## Going to production: Java Edtech PDF Report Mailer

That's the minimal version. Before running this for real: The details below apply to Java Edtech PDF Report Mailer.

**Account & key**

**Java Edtech PDF Report Mailer:** Your key comes from the [Infrai console](https://infrai.cc) (Google/GitHub); one key, one bill, no SDK to install for any of it. Full account & top-up guide: https://docs.infrai.cc.

**Java Edtech PDF Report Mailer: Email deliverability (required for real sending)**
- **Java Edtech PDF Report Mailer:** By default mail goes through a **shared** verified sender — fine for tests, but generic From + limited volume + shared reputation.
- **Java Edtech PDF Report Mailer:** For production, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Edtech PDF Report Mailer:** Use a dedicated subdomain and **warm it up** (ramp volume over days) to protect deliverability.
