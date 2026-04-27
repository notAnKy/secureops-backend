package com.cyberplatform.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // ─── Send report validated notification to client ─────────────────────────
    @Async
    public void sendReportValidated(
            String toEmail,
            String clientName,
            Long requestId,
            String reportContent
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@secureops.com");
            helper.setTo(toEmail);
            helper.setSubject("✅ Report Validated — Request #" + requestId);

            String html = buildReportValidatedEmail(clientName, requestId, reportContent);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Report validated email sent to {} for request #{}", toEmail, requestId);

        } catch (MessagingException e) {
            log.error("Failed to send report validated email to {}: {}", toEmail, e.getMessage());
        }
    }

    // ─── Send password reset email ────────────────────────────────────────────
    @Async
    public void sendPasswordResetEmail(String toEmail, String name, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@secureops.com");
            helper.setTo(toEmail);
            helper.setSubject("🔑 Reset Your SecureOps Password");

            String resetLink = "http://localhost:5173/reset-password?token=" + token;
            String html = buildPasswordResetEmail(name, resetLink);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }

    // ─── HTML template: password reset ───────────────────────────────────────
    private String buildPasswordResetEmail(String name, String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin:0;padding:0;background:#0a0c0f;font-family:'Helvetica Neue',Arial,sans-serif;">
              <div style="max-width:600px;margin:40px auto;background:#0f1218;border:1px solid rgba(0,255,210,0.15);border-radius:16px;overflow:hidden;">
                <div style="background:linear-gradient(135deg,rgba(0,255,210,0.15),rgba(0,255,210,0.05));padding:32px;border-bottom:1px solid rgba(0,255,210,0.12);">
                  <div style="font-size:13px;color:#00ffd2;letter-spacing:0.12em;text-transform:uppercase;margin-bottom:8px;font-family:monospace;">// SecureOps</div>
                  <div style="font-size:26px;font-weight:800;color:#e8edf5;letter-spacing:-0.02em;">Password Reset 🔑</div>
                </div>
                <div style="padding:32px;">
                  <p style="color:#6b7a8d;font-size:14px;line-height:1.7;margin:0 0 20px;">
                    Hello <strong style="color:#e8edf5;">%s</strong>,
                  </p>
                  <p style="color:#6b7a8d;font-size:14px;line-height:1.7;margin:0 0 28px;">
                    We received a request to reset your SecureOps password. Click the button below to set a new password. This link expires in <strong style="color:#e8edf5;">1 hour</strong>.
                  </p>
                  <div style="text-align:center;margin-bottom:28px;">
                    <a href="%s" style="display:inline-block;padding:14px 36px;background:#00ffd2;color:#0a0c0f;font-weight:800;font-size:14px;text-decoration:none;border-radius:10px;letter-spacing:0.03em;">
                      Reset My Password →
                    </a>
                  </div>
                  <div style="background:#131820;border:1px solid rgba(255,255,255,0.06);border-radius:8px;padding:16px 20px;margin-bottom:24px;">
                    <div style="font-family:monospace;font-size:11px;color:#3d4a5c;text-transform:uppercase;letter-spacing:0.1em;margin-bottom:8px;">Or paste this link in your browser</div>
                    <div style="font-family:monospace;font-size:11px;color:#6b7a8d;word-break:break-all;">%s</div>
                  </div>
                  <p style="color:#6b7a8d;font-size:13px;line-height:1.7;margin:0;">
                    If you did not request a password reset, you can safely ignore this email.
                  </p>
                </div>
                <div style="padding:20px 32px;border-top:1px solid rgba(255,255,255,0.05);">
                  <p style="color:#3d4a5c;font-size:11px;font-family:monospace;margin:0;">
                    This link expires in 1 hour. This is an automated message from SecureOps.
                  </p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(name, resetLink, resetLink);
    }

    // ─── Send report invalidated notification to client ───────────────────────
    @Async
    public void sendReportInvalidated(
            String toEmail,
            String clientName,
            Long requestId
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@secureops.com");
            helper.setTo(toEmail);
            helper.setSubject("⚠️ Report Invalidated — Request #" + requestId);

            String html = buildReportInvalidatedEmail(clientName, requestId);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Report invalidated email sent to {} for request #{}", toEmail, requestId);

        } catch (MessagingException e) {
            log.error("Failed to send report invalidated email to {}: {}", toEmail, e.getMessage());
        }
    }

    // ─── HTML email template: report validated ────────────────────────────────
    private String buildReportValidatedEmail(String clientName, Long requestId, String reportContent) {
        String preview = reportContent != null && reportContent.length() > 200
            ? reportContent.substring(0, 200) + "..."
            : reportContent;

        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin:0;padding:0;background:#0a0c0f;font-family:'Helvetica Neue',Arial,sans-serif;">
              <div style="max-width:600px;margin:40px auto;background:#0f1218;border:1px solid rgba(0,255,210,0.15);border-radius:16px;overflow:hidden;">
                
                <!-- Header -->
                <div style="background:linear-gradient(135deg,rgba(0,255,210,0.15),rgba(0,255,210,0.05));padding:32px;border-bottom:1px solid rgba(0,255,210,0.12);">
                  <div style="font-size:13px;color:#00ffd2;letter-spacing:0.12em;text-transform:uppercase;margin-bottom:8px;font-family:monospace;">
                    // SecureOps
                  </div>
                  <div style="font-size:26px;font-weight:800;color:#e8edf5;letter-spacing:-0.02em;">
                    Report Validated ✅
                  </div>
                </div>

                <!-- Body -->
                <div style="padding:32px;">
                  <p style="color:#6b7a8d;font-size:14px;line-height:1.7;margin:0 0 20px;">
                    Hello <strong style="color:#e8edf5;">%s</strong>,
                  </p>
                  <p style="color:#6b7a8d;font-size:14px;line-height:1.7;margin:0 0 24px;">
                    A security report for your request has been reviewed and <strong style="color:#00ffd2;">validated</strong> by our team. It is now available in your client portal.
                  </p>

                  <!-- Request badge -->
                  <div style="background:rgba(0,255,210,0.06);border:1px solid rgba(0,255,210,0.2);border-radius:10px;padding:16px 20px;margin-bottom:24px;">
                    <div style="font-family:monospace;font-size:11px;color:#3d4a5c;text-transform:uppercase;letter-spacing:0.1em;margin-bottom:6px;">Request</div>
                    <div style="font-family:monospace;font-size:16px;font-weight:700;color:#00ffd2;">#%d</div>
                  </div>

                  <!-- Report preview -->
                  %s

                  <p style="color:#6b7a8d;font-size:13px;line-height:1.7;margin:24px 0 0;">
                    Log in to your SecureOps account to view the full report and download it as a PDF.
                  </p>
                </div>

                <!-- Footer -->
                <div style="padding:20px 32px;border-top:1px solid rgba(255,255,255,0.05);">
                  <p style="color:#3d4a5c;font-size:11px;font-family:monospace;margin:0;">
                    This is an automated message from SecureOps. Please do not reply to this email.
                  </p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                clientName,
                requestId,
                preview != null
                    ? """
                      <div style="background:#131820;border:1px solid rgba(255,255,255,0.06);border-radius:8px;padding:16px 20px;margin-bottom:8px;">
                        <div style="font-family:monospace;font-size:11px;color:#3d4a5c;text-transform:uppercase;letter-spacing:0.1em;margin-bottom:8px;">Report preview</div>
                        <div style="font-family:monospace;font-size:12px;color:#6b7a8d;line-height:1.7;">%s</div>
                      </div>
                      """.formatted(preview)
                    : ""
            );
    }

    // ─── HTML email template: report invalidated ──────────────────────────────
    private String buildReportInvalidatedEmail(String clientName, Long requestId) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin:0;padding:0;background:#0a0c0f;font-family:'Helvetica Neue',Arial,sans-serif;">
              <div style="max-width:600px;margin:40px auto;background:#0f1218;border:1px solid rgba(255,77,109,0.2);border-radius:16px;overflow:hidden;">

                <!-- Header -->
                <div style="background:rgba(255,77,109,0.08);padding:32px;border-bottom:1px solid rgba(255,77,109,0.15);">
                  <div style="font-size:13px;color:#00ffd2;letter-spacing:0.12em;text-transform:uppercase;margin-bottom:8px;font-family:monospace;">
                    // SecureOps
                  </div>
                  <div style="font-size:26px;font-weight:800;color:#e8edf5;letter-spacing:-0.02em;">
                    Report Invalidated ⚠️
                  </div>
                </div>

                <!-- Body -->
                <div style="padding:32px;">
                  <p style="color:#6b7a8d;font-size:14px;line-height:1.7;margin:0 0 20px;">
                    Hello <strong style="color:#e8edf5;">%s</strong>,
                  </p>
                  <p style="color:#6b7a8d;font-size:14px;line-height:1.7;margin:0 0 24px;">
                    A report for your request <strong style="color:#ff4d6d;">#%d</strong> has been marked as <strong style="color:#ff4d6d;">invalid</strong> by our team and is no longer visible in your portal. Our specialists will submit a revised report shortly.
                  </p>
                  <p style="color:#6b7a8d;font-size:13px;line-height:1.7;margin:0;">
                    If you have any questions, please contact your SecureOps account manager.
                  </p>
                </div>

                <!-- Footer -->
                <div style="padding:20px 32px;border-top:1px solid rgba(255,255,255,0.05);">
                  <p style="color:#3d4a5c;font-size:11px;font-family:monospace;margin:0;">
                    This is an automated message from SecureOps. Please do not reply to this email.
                  </p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(clientName, requestId);
    }
}