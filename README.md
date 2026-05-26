# 🏨 BookingApp - Microservices Spring Boot

> Application de réservation d'hôtels basée sur une architecture microservices avec Spring Boot, Spring Cloud, Kafka, Flouci et JavaMail.

---

## 📋 Table des matières

- [Architecture](#architecture)
- [Microservices](#microservices)
- [Technologies](#technologies)
- [Config Server](#config-server)
- [Eureka - Service Discovery](#eureka---service-discovery)
- [API Gateway](#api-gateway)
- [Communication HTTP - OpenFeign](#communication-http---openfeign)
- [Kafka - Messaging Asynchrone](#kafka---messaging-asynchrone)
- [Paiement - Intégration Flouci](#paiement---intégration-flouci)
- [Notification - Envoi Email](#notification---envoi-email)
- [Docker Compose](#docker-compose)
- [Variables d'environnement](#variables-denvironnement)
- [Endpoints REST](#endpoints-rest)
- [Démarrage rapide](#démarrage-rapide)

---

## Architecture

```
Client
  │
  ▼
API Gateway (8080)
  │
  ├──► Customer Service (8081)  ──► PostgreSQL
  ├──► Hotel Service    (8082)  ──► PostgreSQL
  ├──► Booking Service  (8083)  ──► PostgreSQL ──[Kafka]──► Notification (8085) ──► MongoDB
  └──► Payment Service  (8084)  ──► PostgreSQL ──[Flouci API]

Config Server (8888) ◄── tous les services
Eureka Discovery (8761) ◄── tous les services
```

---

## Microservices

| Service | Port | Rôle | Base de données |
|---|---|---|---|
| `config-server` | 8888 | Centralisation de la configuration | Git / Filesystem |
| `discovery` | 8761 | Registre Eureka (Service Discovery) | — |
| `gateway` | 8080 | Point d'entrée unique (Spring Cloud Gateway) | — |
| `customer` | 8081 | Gestion des clients et authentification | PostgreSQL |
| `hotel` | 8082 | Gestion des hôtels et chambres | PostgreSQL |
| `booking` | 8083 | Gestion des réservations | PostgreSQL |
| `payment` | 8084 | Paiement via API Flouci | PostgreSQL |
| `notification` | 8085 | Envoi d'emails (JavaMail + Thymeleaf) | MongoDB |

---

## Technologies

- **Java 17** / Spring Boot 3.x
- **Spring Cloud** : Config Server, Eureka, Gateway, OpenFeign
- **Apache Kafka** — messaging asynchrone entre services
- **OpenFeign** — communication HTTP synchrone inter-services
- **Flouci API** — paiement en ligne (Tunisie)
- **JavaMailSender + Thymeleaf** — emails transactionnels
- **Docker & Docker Compose** — containerisation
- **PostgreSQL** — base de données des services métier
- **MongoDB** — stockage des notifications

---

## Config Server

Le Config Server centralise toutes les configurations. Il doit **démarrer en premier**.

### Dépendance Maven

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

### application.yml

```yaml
server:
  port: 8888

spring:
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/configurations
```

### Classe principale

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication { ... }
```

### Connexion depuis les autres services

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
  application:
    name: booking-service
```

---

## Eureka - Service Discovery

Eureka permet aux microservices de se découvrir mutuellement sans adresses IP en dur.

### Dépendance Maven (serveur)

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

### application.yml (serveur)

```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

### Classe principale

```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryApplication { ... }
```

### Enregistrement des clients (chaque service)

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
  instance:
    prefer-ip-address: true
```

Dashboard Eureka : http://localhost:8761

---

## API Gateway

Point d'entrée unique. Route les requêtes vers les bons microservices via Eureka (`lb://`).

### Dépendances Maven

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### Routes (application.yml)

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: customer-service
          uri: lb://CUSTOMER-SERVICE
          predicates:
            - Path=/api/v1/customers/**

        - id: hotel-service
          uri: lb://HOTEL-SERVICE
          predicates:
            - Path=/api/v1/hotels/**

        - id: booking-service
          uri: lb://BOOKING-SERVICE
          predicates:
            - Path=/api/v1/bookings/**

        - id: payment-service
          uri: lb://PAYMENT-SERVICE
          predicates:
            - Path=/api/v1/payments/**
```

---

## Communication HTTP - OpenFeign

Les microservices communiquent de façon synchrone via OpenFeign. Exemples : Booking → Hotel (vérifier disponibilité), Booking → Customer (récupérer les infos client).

### Dépendance Maven

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### Activation

```java
@SpringBootApplication
@EnableFeignClients
public class BookingApplication { ... }
```

### Exemple : BookingService → HotelService

```java
@FeignClient(name = "HOTEL-SERVICE", url = "${application.config.hotel-url}")
public interface HotelClient {

    @GetMapping("/api/v1/hotels/rooms/{roomId}/availability")
    boolean checkRoomAvailability(@PathVariable Integer roomId);

    @PutMapping("/api/v1/hotels/rooms/{roomId}/book")
    void bookRoom(@PathVariable Integer roomId);
}
```

### Exemple : BookingService → CustomerService

```java
@FeignClient(name = "CUSTOMER-SERVICE", url = "${application.config.customer-url}")
public interface CustomerClient {

    @GetMapping("/api/v1/customers/{customerId}")
    CustomerResponse findById(@PathVariable String customerId);
}
```

---

## Kafka - Messaging Asynchrone

Kafka gère la communication asynchrone. Quand une réservation est confirmée, Booking publie un événement que Notification consomme pour envoyer l'email.

### Dépendance Maven

```xml
<dependency>
  <groupId>org.springframework.kafka</groupId>
  <artifactId>spring-kafka</artifactId>
</dependency>
```

### Configuration Producer (booking-service)

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        spring.json.add.type.headers: false
```

### Configuration Consumer (notification-service)

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notificationGroup
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: '*'
```

### Producer — BookingService publie l'événement

```java
@Service
@RequiredArgsConstructor
public class BookingProducer {

    private final KafkaTemplate<String, BookingConfirmation> kafkaTemplate;

    public void sendBookingConfirmation(BookingConfirmation confirmation) {
        Message<BookingConfirmation> message = MessageBuilder
            .withPayload(confirmation)
            .setHeader(KafkaHeaders.TOPIC, "booking-topic")
            .build();
        kafkaTemplate.send(message);
    }
}
```

### Consumer — NotificationService écoute

```java
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;
    private final NotificationRepository repository;

    @KafkaListener(topics = "booking-topic", groupId = "notificationGroup")
    public void consumeBookingConfirmation(BookingConfirmation confirmation) {
        emailService.sendBookingConfirmationEmail(confirmation);
        repository.save(Notification.builder()
            .type(NotificationType.BOOKING_CONFIRMATION)
            .bookingReference(confirmation.bookingReference())
            .sentAt(LocalDateTime.now())
            .build());
    }
}
```

### DTO partagé

```java
public record BookingConfirmation(
    String bookingReference,
    String customerName,
    String customerEmail,
    String hotelName,
    LocalDate checkInDate,
    LocalDate checkOutDate,
    BigDecimal totalAmount
) {}
```

---

## Paiement - Intégration Flouci

Flouci est une API de paiement tunisienne. Le flux se déroule en deux étapes : initialisation puis vérification.

### Flux de paiement

```
Client ──► BookingService ──[Feign]──► PaymentService
                                            │
                                    POST /payment/initiate
                                            │
                                       Flouci API
                                            │
                                    ◄── payment_url
                                            │
                               Client redirigé vers Flouci
                                            │
                               Paiement ──► success_link ou fail_link
                                            │
                               PaymentService vérifie via Flouci
                                            │
                               Kafka ──► Notification ──► Email
```

### Configuration .env

```env
FLOUCI_APP_TOKEN=your_app_token
FLOUCI_APP_SECRET=your_app_secret
FLOUCI_API_URL=https://developers.flouci.com/api
```

### DTO de requête Flouci

```java
@Data
public class FlouciPaymentRequest {
    private String app_token;
    private String app_secret;
    private BigDecimal amount;        // en millimes (1 TND = 1000)
    private boolean accept_card;
    private String success_link;
    private String fail_link;
    private String session_timeout_secs;
    private String developer_tracking_id;
}
```

### Service d'intégration

```java
@Service
@RequiredArgsConstructor
public class FlouciPaymentService {

    private final RestTemplate restTemplate;

    @Value("${flouci.api-url}") private String apiUrl;
    @Value("${flouci.app-token}") private String appToken;
    @Value("${flouci.app-secret}") private String appSecret;

    public String initiatePayment(PaymentRequest request) {
        FlouciPaymentRequest flouciReq = FlouciPaymentRequest.builder()
            .app_token(appToken)
            .app_secret(appSecret)
            .amount(request.amount().multiply(BigDecimal.valueOf(1000)))
            .accept_card(true)
            .success_link("http://localhost:8080/api/v1/payments/success")
            .fail_link("http://localhost:8080/api/v1/payments/fail")
            .developer_tracking_id(request.reference())
            .build();

        FlouciPaymentResponse response = restTemplate.postForObject(
            apiUrl + "/payment/initiate",
            flouciReq,
            FlouciPaymentResponse.class
        );

        return response.getResult().getLink();  // URL de paiement Flouci
    }

    public boolean verifyPayment(String paymentId) {
        String url = apiUrl + "/payment/" + paymentId + "/verify"
            + "?app_token=" + appToken;
        FlouciVerifyResponse resp = restTemplate.getForObject(
            url, FlouciVerifyResponse.class
        );
        return resp != null && "SUCCESS".equals(resp.getResult().getStatus());
    }
}
```

### Controller Payment

```java
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final FlouciPaymentService flouciService;
    private final BookingProducer bookingProducer;

    @PostMapping
    public ResponseEntity<String> initiatePayment(@RequestBody PaymentRequest request) {
        String paymentUrl = flouciService.initiatePayment(request);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/success")
    public ResponseEntity<Void> paymentSuccess(@RequestParam("payment_id") String paymentId) {
        boolean verified = flouciService.verifyPayment(paymentId);
        if (verified) {
            // publier événement de confirmation
            bookingProducer.sendBookingConfirmation(/* ... */);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fail")
    public ResponseEntity<Void> paymentFailed(@RequestParam("payment_id") String paymentId) {
        // gérer l'échec
        return ResponseEntity.ok().build();
    }
}
```

---

## Notification - Envoi Email

Le service Notification écoute les événements Kafka et envoie des emails HTML via JavaMailSender et Thymeleaf.

### Dépendances Maven

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

### Configuration SMTP (Gmail)

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

> ⚠️ Pour Gmail, activer la validation en deux étapes puis générer un **App Password** dans les paramètres du compte.

### Service Email

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendBookingConfirmationEmail(BookingConfirmation confirmation) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@bookingapp.com");
            helper.setTo(confirmation.customerEmail());
            helper.setSubject("Confirmation de réservation #" + confirmation.bookingReference());

            Context ctx = new Context();
            ctx.setVariable("customerName", confirmation.customerName());
            ctx.setVariable("hotelName", confirmation.hotelName());
            ctx.setVariable("checkIn", confirmation.checkInDate());
            ctx.setVariable("checkOut", confirmation.checkOutDate());
            ctx.setVariable("totalAmount", confirmation.totalAmount());
            ctx.setVariable("reference", confirmation.bookingReference());

            String htmlContent = templateEngine.process("booking-confirmation", ctx);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email envoyé à {}", confirmation.customerEmail());

        } catch (Exception e) {
            log.error("Erreur envoi email : {}", e.getMessage());
        }
    }
}
```

### Template Thymeleaf (`resources/templates/booking-confirmation.html`)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
  <h2>Bonjour <span th:text="${customerName}"></span>,</h2>
  <p>Votre réservation est confirmée !</p>
  <ul>
    <li>Hôtel : <strong th:text="${hotelName}"></strong></li>
    <li>Check-in : <span th:text="${checkIn}"></span></li>
    <li>Check-out : <span th:text="${checkOut}"></span></li>
    <li>Montant total : <strong th:text="${totalAmount} + ' TND'"></strong></li>
    <li>Référence : <span th:text="${reference}"></span></li>
  </ul>
  <p>Merci d'avoir choisi BookingApp !</p>
</body>
</html>
```

---

## Docker Compose

### Structure `docker-compose.yml`

```yaml
version: '3.8'

services:

  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on: [zookeeper]
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  postgres-customer:
    image: postgres:15
    environment:
      POSTGRES_DB: customer_db
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "5432:5432"

  postgres-hotel:
    image: postgres:15
    environment:
      POSTGRES_DB: hotel_db
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "5433:5432"

  postgres-booking:
    image: postgres:15
    environment:
      POSTGRES_DB: booking_db
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "5434:5432"

  postgres-payment:
    image: postgres:15
    environment:
      POSTGRES_DB: payment_db
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "5435:5432"

  mongodb:
    image: mongo:6
    ports:
      - "27017:27017"

  config-server:
    build: ./services/config-server
    ports:
      - "8888:8888"

  discovery:
    build: ./services/discovery
    ports:
      - "8761:8761"
    depends_on: [config-server]

  gateway:
    build: ./services/gateway
    ports:
      - "8080:8080"
    depends_on: [discovery]

  customer-service:
    build: ./services/customer
    ports:
      - "8081:8081"
    depends_on: [postgres-customer, discovery, config-server]

  hotel-service:
    build: ./services/hotel
    ports:
      - "8082:8082"
    depends_on: [postgres-hotel, discovery, config-server]

  booking-service:
    build: ./services/booking
    ports:
      - "8083:8083"
    depends_on: [postgres-booking, kafka, discovery, config-server]

  payment-service:
    build: ./services/payment
    ports:
      - "8084:8084"
    depends_on: [postgres-payment, discovery, config-server]

  notification-service:
    build: ./services/notification
    ports:
      - "8085:8085"
    depends_on: [mongodb, kafka, discovery, config-server]
```

### Commandes utiles

```bash
# Démarrer tous les services
docker-compose up -d

# Voir les logs d'un service
docker-compose logs -f booking-service

# Redémarrer un service
docker-compose restart notification-service

# Arrêter tout
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

### Ordre de démarrage (sans Docker)

1. `config-server` (8888)
2. `discovery` (8761)
3. `gateway` (8080)
4. `customer`, `hotel`, `booking`, `payment`, `notification`

---

## Variables d'environnement

Copier `.env.example` vers `.env` et remplir les valeurs :

```env
# Config Server
CONFIG_SERVER_URL=http://localhost:8888

# Eureka
EUREKA_URL=http://localhost:8761/eureka

# PostgreSQL
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Flouci (Paiement)
FLOUCI_APP_TOKEN=your_app_token
FLOUCI_APP_SECRET=your_app_secret
FLOUCI_API_URL=https://developers.flouci.com/api

# Email (Gmail SMTP)
MAIL_USERNAME=your@gmail.com
MAIL_PASSWORD=your_app_password
MAIL_FROM=noreply@bookingapp.com
```

---

## Endpoints REST

Tous les appels passent par le Gateway : `http://localhost:8080`

### Customer Service

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/customers` | Créer un client |
| `GET` | `/api/v1/customers/{id}` | Récupérer un client |
| `PUT` | `/api/v1/customers/{id}` | Mettre à jour |
| `GET` | `/api/v1/customers/exists/{id}` | Vérifier l'existence |

### Hotel Service

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/hotels` | Créer un hôtel |
| `GET` | `/api/v1/hotels` | Lister les hôtels |
| `GET` | `/api/v1/hotels/{id}` | Détail d'un hôtel |
| `GET` | `/api/v1/hotels/{id}/rooms` | Chambres d'un hôtel |
| `GET` | `/api/v1/hotels/rooms/{id}/availability` | Disponibilité d'une chambre |

### Booking Service

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/bookings` | Créer une réservation |
| `GET` | `/api/v1/bookings/{id}` | Détail d'une réservation |
| `GET` | `/api/v1/bookings/customer/{id}` | Réservations d'un client |
| `PATCH` | `/api/v1/bookings/{id}/cancel` | Annuler une réservation |

### Payment Service

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/payments` | Initier un paiement Flouci |
| `GET` | `/api/v1/payments/success` | Callback succès Flouci |
| `GET` | `/api/v1/payments/fail` | Callback échec Flouci |
| `GET` | `/api/v1/payments/{id}` | Détail d'un paiement |

---

## Démarrage rapide

### Prérequis

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Compte Gmail avec App Password
- Compte développeur [Flouci](https://developers.flouci.com)

### Installation

```bash
# 1. Cloner le projet
git clone <repo-url>
cd BookingApp

# 2. Configurer les variables d'environnement
cp .env.example .env
# Éditer .env avec vos valeurs (Flouci token, Gmail password, etc.)

# 3. Build et démarrage
docker-compose up -d --build

# 4. Vérifier que tout est UP
docker-compose ps
```

### Vérification

```bash
# Eureka Dashboard
open http://localhost:8761

# Vérifier la config d'un service
curl http://localhost:8888/booking-service/default

# Health check via Gateway
curl http://localhost:8080/actuator/health

# Créer un client (test)
curl -X POST http://localhost:8080/api/v1/customers \
  -H 'Content-Type: application/json' \
  -d '{"firstName":"Ahmed","lastName":"Ben Ali","email":"ahmed@test.com","phone":"55123456"}'
```

---

## Structure du projet

```
BookingApp/
├── services/
│   ├── config-server/        # Spring Cloud Config Server
│   ├── discovery/            # Eureka Server
│   ├── gateway/              # API Gateway
│   ├── customer/             # Service Client
│   ├── hotel/                # Service Hôtel
│   ├── booking/              # Service Réservation
│   ├── payment/              # Service Paiement (Flouci)
│   └── notification/         # Service Email (Kafka Consumer)
├── diagrams/                 # Diagrammes d'architecture
├── .env                      # Variables d'environnement
└── docker-compose.yml        # Orchestration Docker
```

---

*BookingApp — Spring Boot Microservices | Spring Cloud | Kafka | Flouci | JavaMail*
