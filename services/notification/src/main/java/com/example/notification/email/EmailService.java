package com.example.notification.email;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendPaymentSuccessEmail(
            String destinationEmail,
            String customerName,
            int amount,
            String reference
    ) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper =
                    new MimeMessageHelper(
                            mimeMessage,
                            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                            UTF_8.name()
                    );

            messageHelper.setFrom("contact@booking.com");
            messageHelper.setTo(destinationEmail);
            messageHelper.setSubject("Payment Confirmation");

            // Variables du template
            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", customerName);
            variables.put("amount", amount);
            variables.put("Reference", reference);

            // Correct Context import pour Thymeleaf
            Context context = new Context();
            context.setVariables(variables);

            String templateName = "payment-success"; // payment-success.html
            String htmlContent = templateEngine.process(templateName, context);

            messageHelper.setText(htmlContent, true);
            mailSender.send(mimeMessage);

            log.info("Email successfully sent to {}", destinationEmail);

        } catch (MessagingException e) {
            log.error("Cannot send email to {}", destinationEmail, e);
        }
    }
}
