package com.Bookingapp.payment.payment;


import com.Bookingapp.payment.notification.NotificationProducer;
import com.Bookingapp.payment.notification.PaymentConfirmation;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final NotificationProducer notificationProducer;

    @Value("${flouci.app_token}")
    private String appToken;

    @Value("${flouci.app_secret}")
    private String appSecret;

    @Value("${flouci.developer_tracking_id}")
    private String developerTrackingId;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://developers.flouci.com/api/v2")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    /**
     * Génère un paiement via l'API Flouci
     */
    public Result generatePayment(Integer amount) {
        Request request = new Request(
                appToken,
                appSecret,
                "true",
                amount,
                "http://localhost:8080/payment/success",
                "http://localhost:8080/payment/error",
                1200,
                developerTrackingId
        );

        PaymentResponse response = webClient.post()
                .uri("/generate_payment")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + appToken + ":" + appSecret)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();

        return response.result();
    }

    /**
     * Vérifie le paiement via l'API Flouci
     */
    public boolean verifyPayment(String paymentId) {
        return webClient.get()
                .uri("/verify_payment/{paymentId}", paymentId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + appToken + ":" + appSecret)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> node.get("success").asBoolean(false))
                .block();
    }

    /**
     * Crée un paiement, sauvegarde en DB et envoie une notification Kafka
     */
    public Payment createPayment(PaymentRequest paymentRequest) {
        // 1️⃣ Générer le paiement
        Result res = generatePayment(paymentRequest.amount());

        // 2️⃣ Vérifier le paiement
        boolean isVerified = verifyPayment(res.payment_id());
        if (!isVerified) {
            throw new RuntimeException("Le paiement n'a pas pu être vérifié");
        }

        // 3️⃣ Sauvegarder le paiement dans la DB
        Payment payment = Payment.builder()
                .amount(paymentRequest.amount())
                .Bookingreference(paymentRequest.reference())
                .createdDate(LocalDateTime.now())
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        PaymentConfirmation p = new PaymentConfirmation(
                paymentRequest.reference(),
                paymentRequest.amount(),
                paymentRequest.customer().firstName(),
                paymentRequest.customer().lastName(),
                paymentRequest.customer().email()


        );


        notificationProducer.sendNotification(p);

        // Retourner le paiement sauvegardé
        return savedPayment;
    }
}
