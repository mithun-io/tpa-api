package com.tpa.helper;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${admin.email}")
    private String adminEmail;

    private void sendEmail(String to, String subject, String htmlContent) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom(adminEmail, "TPA - Insurance Claim Processing System");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }

    @Async
    public void sendOtp(String name, String email, Integer otp) {
        try {
            String subject = "TPA Account Creation - OTP Verification";

            String text = "<html><body>"
                    + "<h2>TPA - Insurance Claim Processing System</h2>"
                    + "<p>Dear <b>" + name + "</b>,</p>"
                    + "<p>Your One Time Password (OTP) for account registration is:</p>"
                    + "<h3>" + otp + "</h3>"
                    + "<p>This OTP is valid for exactly <b>5 minutes</b>. "
                    + "<br/>"
                    + "<p>Best regards,<br/>TPA - Insurance Claim Processing System Team</p>"
                    + "</body></html>";

            sendEmail(email, subject, text);

        } catch (Exception e) {
            log.error("Failed to send OTP email to {}", email, e);
        }
    }

    @Async
    public void sendPatientRegistrationOtp(String name, String email, Integer otp) {
        try {
            String subject = "TPA Patient Registration - OTP Verification";

            String text = "<html><body>"
                    + "<h2>TPA - Insurance Claim Processing System</h2>"
                    + "<p>Dear <b>" + name + "</b>,</p>"
                    + "<p>Your One Time Password (OTP) for patient account registration is:</p>"
                    + "<h3>" + otp + "</h3>"
                    + "<p>This OTP is valid for exactly <b>5 minutes</b>.</p>"
                    + "<br/>"
                    + "<p>Best regards,<br/>TPA - Insurance Claim Processing System Team</p>"
                    + "</body></html>";

            sendEmail(email, subject, text);

        } catch (Exception e) {
            log.error("Failed to send patient registration OTP email to {}", email, e);
        }
    }

    @Async
    public void sendCarrierRegistrationOtp(String companyName, String email, Integer otp) {
        try {
            String subject = "TPA Carrier Registration - OTP Verification";

            String text = "<html><body>"
                    + "<h2>TPA - Insurance Claim Processing System</h2>"
                    + "<p>Dear <b>" + companyName + "</b>,</p>"
                    + "<p>Your One Time Password (OTP) for carrier account registration is:</p>"
                    + "<h3>" + otp + "</h3>"
                    + "<p>This OTP is valid for exactly <b>5 minutes</b>.</p>"
                    + "<br/>"
                    + "<p>Best regards,<br/>TPA - Insurance Claim Processing System Team</p>"
                    + "</body></html>";

            sendEmail(email, subject, text);

        } catch (Exception e) {
            log.error("Failed to send carrier registration OTP email to {}", email, e);
        }
    }

    @Async
    public void sendConfirmation(String name, String email) {
        try {
            String subject = "TPA Registration Successful";

            String text = "<html><body>"
                    + "<h2>Welcome to TPA - Insurance Claim Processing System</h2>"
                    + "<p>Dear <b>" + name + "</b>,</p>"
                    + "<p>Your account has been successfully registered.</p>"
                    + "<p>You can now log in to the portal using your registered email address:</p>"
                    + "<ul>"
                    + "<li><b>Email:</b> " + email + "</li>"
                    + "</ul>"
                    + "<br/>"
                    + "<p>Best regards,<br/>TPA - Insurance Claim Processing System Team</p>"
                    + "</body></html>";

            sendEmail(email, subject, text);

        } catch (Exception e) {
            log.error("Failed to send confirmation email to {}", email, e);
        }
    }

    @Async
    public void sendPatientRegistrationConfirmation(String name, String email) {
        try {
            String subject = "TPA Patient Registration Successful";

            String text = "<html><body>"
                    + "<h2>Welcome to TPA - Insurance Claim Processing System</h2>"
                    + "<p>Dear <b>" + name + "</b>,</p>"
                    + "<p>Your patient account has been successfully registered.</p>"
                    + "<p>You can now log in using your registered email address:</p>"
                    + "<ul>"
                    + "<li><b>Email:</b> " + email + "</li>"
                    + "</ul>"
                    + "<br/>"
                    + "<p>Best regards,<br/>TPA - Insurance Claim Processing System Team</p>"
                    + "</body></html>";

            sendEmail(email, subject, text);

        } catch (Exception e) {
            log.error("Failed to send patient registration confirmation email to {}", email, e);
        }
    }

    @Async
    public void sendCarrierRegistrationConfirmation(String companyName, String email) {
        try {
            String subject = "TPA Carrier Registration Received";

            String text = "<html><body>"
                    + "<h2>Carrier Registration Received</h2>"
                    + "<p>Dear <b>" + companyName + "</b>,</p>"
                    + "<p>Your carrier registration has been submitted successfully and is pending admin approval.</p>"
                    + "<p>You will receive a separate email after the review is complete.</p>"
                    + "<ul>"
                    + "<li><b>Email:</b> " + email + "</li>"
                    + "</ul>"
                    + "<br/>"
                    + "<p>Best regards,<br/>TPA - Insurance Claim Processing System Team</p>"
                    + "</body></html>";

            sendEmail(email, subject, text);

        } catch (Exception e) {
            log.error("Failed to send carrier registration confirmation email to {}", email, e);
        }
    }

    @Async
    public void sendPaymentConfirmation(String email, Long orderId, Double amount) {
        try {
            String subject = "TPA Payment Successful";

            String text = "<html><body>"
                    + "<h2>Payment Received</h2>"
                    + "<p>Your payment has been successfully processed.</p>"
                    + "<ul>"
                    + "<li><b>Reference ID:</b> " + orderId + "</li>"
                    + "<li><b>Amount Paid:</b> $" + amount + "</li>"
                    + "</ul>"
                    + "<br/>"
                    + "<p>Thank you,<br/>TPA - Insurance Claim Processing System Team</p>"
                    + "</body></html>";

            sendEmail(email, subject, text);

        } catch (Exception e) {
            log.error("Failed to send payment email to {}", email, e);
        }
    }

    @Async("taskExecutor")
    public void sendClaimStatusNotification(String email, Long claimId, String status, String messageStr) {
        try {
            String subject = "TPA Claim #" + claimId + " Status Update: " + status;

            String text = "<html><body>"
                    + "<h2>Claim Status Update</h2>"
                    + "<p>Hello,</p>"
                    + "<p>Your claim <b>#" + claimId + "</b> has been updated to: "
                    + "<b>" + status + "</b></p>"
                    + "<p><b>Details:</b></p>"
                    + "<p>" + messageStr + "</p>"
                    + "<br/>"
                    + "<p>Best regards,<br/>TPA - Insurance Claim Processing System Team</p>"
                    + "</body></html>";

            sendEmail(email, subject, text);

        } catch (Exception e) {
            log.error("Failed to send claim notification email to {}", email, e);
        }
    }

    @Async
    public void sendCarrierApprovalEmail(String email, String companyName) {
        try {
            String subject = "Your Carrier Application Has Been Approved — TPA Claim System";

            String text = "<html><body>"
                    + "<h1>Application Approved!</h1>"
                    + "<p>Dear <b>" + companyName + "</b>,</p>"
                    + "<p>We are pleased to inform you that your carrier registration application on the "
                    + "<b>TPA - Insurance Claim Processing System</b> has been <b>APPROVED</b> by our admin team.</p>"
                    + "<p><b>Your account is now active.</b></p>"
                    + "<p>You can now log in to the carrier portal using your registered email and password.</p>"
                    + "<br/>"
                    + "<p>Best regards,<br/><b>TPA - Insurance Claim Processing System Team</b></p>"
                    + "</body></html>";

            sendEmail(email, subject, text);
            log.info("Carrier approval email sent to {}", email);

        } catch (Exception e) {
            log.error("Failed to send carrier approval email to {}", email, e);
        }
    }

    @Async
    public void sendCarrierRejectionEmail(String email, String companyName) {
        try {
            String subject = "Your Carrier Application Status — TPA Claim System";

            String text = "<html><body>"
                    + "<h1>Application Not Approved</h1>"
                    + "<p>Dear <b>" + companyName + "</b>,</p>"
                    + "<p>Thank you for applying to join the <b>TPA - Insurance Claim Processing System</b> carrier network.</p>"
                    + "<p>After reviewing your application, our compliance team has determined that "
                    + "we are unable to approve your carrier registration at this time.</p>"
                    + "<p><b>Application Status:</b> Not Approved</p>"
                    + "<br/>"
                    + "<p>Best regards,<br/><b>TPA - Insurance Claim Processing System Team</b></p>"
                    + "</body></html>";

            sendEmail(email, subject, text);
            log.info("Carrier rejection email sent to {}", email);

        } catch (Exception e) {
            log.error("Failed to send carrier rejection email to {}", email, e);
        }
    }

    @Async("taskExecutor")
    public void sendSimpleEmail(String to, String subject, String bodyText) {
        try {
            String text = "<html><body>"
                    + "<h2>TPA - Insurance Claim Processing System — System Alert</h2>"
                    + "<p>" + bodyText + "</p>"
                    + "<br/>"
                    + "<p>This is an automated system alert. Do not reply to this email.</p>"
                    + "</body></html>";

            sendEmail(to, subject, text);

        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", to, e.getMessage());
        }
    }
}
