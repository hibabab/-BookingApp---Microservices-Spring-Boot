# 🏨 BookingApp - Microservices Spring Boot

> Application de réservation d'hôtels basée sur une architecture microservices avec Spring Boot, Spring Cloud, Kafka, Flouci et JavaMail.

---

## Microservices

| Service | Port | Rôle | Base de données |
|---|---|---|---|
| `config-server` | 8888 | Centralisation de la configuration | Git / Filesystem |
| `discovery` | 8761 | Registre Eureka (Service Discovery) | — |
| `gateway` | 8222 | Point d'entrée unique (Spring Cloud Gateway) | — |
| `customer` | 8090 | Gestion des clients et authentification | PostgreSQL |
| `hotel` | 8050 | Gestion des hôtels et chambres | PostgreSQL |
| `booking` | 8222 | Gestion des réservations | PostgreSQL |
| `payment` | 8060 | Paiement via API Flouci | PostgreSQL |
| `notification` | 8040 | Envoi d'emails (JavaMail + Thymeleaf) | MongoDB |

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

## Architecture par feature (approche adoptée)

Chaque microservice est organisé **par feature** (et non par couche technique). Chaque dossier de feature regroupe tous les éléments qui lui appartiennent :

```
feature/
├── Entity.java          # Entité JPA / Document MongoDB
├── EntityDto.java        # DTO (Request / Response)
├── EntityMapper.java     # Mapper (MapStruct ou manuel)
├── EntityRepository.java # Repository (JPA / MongoDB)
├── EntityService.java    # Logique métier
└── EntityController.java # Endpoints REST
```

---

## Fonctionnalités détaillées par service

---

### ⚙️ config-server — Port 8888

Centralise toutes les configurations de l'application. Chaque service récupère sa configuration au démarrage.

```
config-server/
└── src/main/
    ├── resources/
    │   └── configurations/
    │       ├── customer-service.yml
    │       ├── hotel-service.yml
    │       ├── booking-service.yml
    │       ├── payment-service.yml
    │       └── notification-service.yml
    └── java/
        └── ConfigServerApplication.java   # @EnableConfigServer
```

**Fonctionnalités :**
- Centralisation des configurations (ports, datasources, Kafka, mail, Flouci)
- Rechargement à chaud sans redémarrage (`@RefreshScope`)
- Stockage natif (classpath) ou Git

---

### 🔍 discovery — Port 8761

Registre Eureka. Tous les services s'y enregistrent et se découvrent mutuellement.

```
discovery/
└── src/main/java/
    └── DiscoveryApplication.java   # @EnableEurekaServer
```

**Fonctionnalités :**
- Enregistrement automatique des instances de microservices
- Load balancing côté client via `lb://SERVICE-NAME`
- Dashboard de monitoring : http://localhost:8761

---

### 🌐 gateway — Port 8222

Point d'entrée unique de l'application. Route les requêtes vers les bons services.

```
gateway/
└── src/main/
    ├── resources/
    │   └── application.yml    # Routes configurées
    └── java/
        └── GatewayApplication.java
```

**Fonctionnalités :**
- Routage dynamique vers les microservices via Eureka (`lb://`)
- Filtres globaux (authentification, logging, CORS)
- Rate limiting

**Routes configurées :**

| Préfixe | Service cible |
|---|---|
| `/api/v1/customers/**` | `CUSTOMER-SERVICE` |
| `/api/v1/hotels/**` | `HOTEL-SERVICE` |
| `/api/v1/bookings/**` | `BOOKING-SERVICE` |
| `/api/v1/payments/**` | `PAYMENT-SERVICE` |

---

### 👤 customer-service — Port 8090

Gestion complète des clients. Exposé aux autres services via Feign.

```
customer/
└── src/main/java/com/bookingapp/customer/
    └── customer/
        ├── Customer.java              # Entité JPA
        ├── CustomerDto.java           # CustomerRequest / CustomerResponse
        ├── CustomerMapper.java        # Entity ↔ DTO
        ├── CustomerRepository.java    # JpaRepository<Customer, String>
        ├── CustomerService.java       # Logique métier
        └── CustomerController.java    # REST /api/v1/customers
```

**Fonctionnalités :**
- Créer, modifier, supprimer un client
- Récupérer un client par ID (appelé par BookingService via Feign)
- Vérifier l'existence d'un client (`GET /exists/{id}`)
- Validation des données (`@Valid`, `@NotBlank`, email format)

**Endpoints :**

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/customers` | Créer un client |
| `GET` | `/api/v1/customers/{id}` | Récupérer un client |
| `PUT` | `/api/v1/customers/{id}` | Mettre à jour |
| `GET` | `/api/v1/customers/exists/{id}` | Vérifier l'existence |

---

### 🏨 hotel-service — Port 8050

Gestion des hôtels et des chambres. Vérifie et met à jour les disponibilités.

```
hotel/
└── src/main/java/com/bookingapp/hotel/
    ├── hotel/
    │   ├── Hotel.java
    │   ├── HotelDto.java
    │   ├── HotelMapper.java
    │   ├── HotelRepository.java
    │   ├── HotelService.java
    │   └── HotelController.java       # REST /api/v1/hotels
    └── room/
        ├── Room.java
        ├── RoomDto.java
        ├── RoomMapper.java
        ├── RoomRepository.java
        ├── RoomService.java
        └── RoomController.java        # REST /api/v1/hotels/rooms
```

**Fonctionnalités :**
- CRUD complet des hôtels (nom, adresse, étoiles, description)
- CRUD des chambres (numéro, type, prix par nuit, capacité)
- Vérifier la disponibilité d'une chambre (appelé par BookingService via Feign)
- Mettre à jour le statut d'une chambre (disponible / réservée)
- Lister les chambres disponibles par dates et critères

**Endpoints :**

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/hotels` | Créer un hôtel |
| `GET` | `/api/v1/hotels` | Lister les hôtels |
| `GET` | `/api/v1/hotels/{id}` | Détail d'un hôtel |
| `GET` | `/api/v1/hotels/{id}/rooms` | Chambres d'un hôtel |
| `GET` | `/api/v1/hotels/rooms/{id}/availability` | Disponibilité d'une chambre |
| `PUT` | `/api/v1/hotels/rooms/{id}/book` | Marquer une chambre réservée |
| `PUT` | `/api/v1/hotels/rooms/{id}/release` | Libérer une chambre |

---

### 📅 booking-service — Port 8222

Orchestre le processus de réservation. Communique avec Customer, Hotel et Payment via Feign, puis publie sur Kafka.

```
booking/
└── src/main/java/com/bookingapp/booking/
    ├── booking/
    │   ├── Booking.java
    │   ├── BookingDto.java            # BookingRequest / BookingResponse
    │   ├── BookingMapper.java
    │   ├── BookingRepository.java
    │   ├── BookingService.java
    │   └── BookingController.java     # REST /api/v1/bookings
    └── kafka/
        ├── BookingConfirmation.java   # Record partagé via Kafka
        └── BookingProducer.java       # KafkaTemplate → "booking-topic"
```

**Fonctionnalités :**
- Créer une réservation (vérifie client via Feign → CustomerService)
- Vérifier la disponibilité de la chambre (via Feign → HotelService)
- Bloquer la chambre après confirmation (via Feign → HotelService)
- Initier le paiement (via Feign → PaymentService)
- Publier un événement Kafka (`booking-topic`) après paiement confirmé
- Annuler une réservation et libérer la chambre
- Lister les réservations d'un client

**Endpoints :**

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/bookings` | Créer une réservation |
| `GET` | `/api/v1/bookings/{id}` | Détail d'une réservation |
| `GET` | `/api/v1/bookings/customer/{id}` | Réservations d'un client |
| `PATCH` | `/api/v1/bookings/{id}/cancel` | Annuler une réservation |

**Clients Feign utilisés :**

```java
@FeignClient(name = "CUSTOMER-SERVICE")
// → findById(customerId)

@FeignClient(name = "HOTEL-SERVICE")
// → checkRoomAvailability(roomId)
// → bookRoom(roomId)
// → releaseRoom(roomId)

@FeignClient(name = "PAYMENT-SERVICE")
// → initiatePayment(paymentRequest)
```

---

### 💳 payment-service — Port 8060

Intègre l'API Flouci pour le traitement des paiements. Gère le cycle complet : initiation, callback et vérification.

```
payment/
└── src/main/java/com/bookingapp/payment/
    ├── payment/
    │   ├── Payment.java
    │   ├── PaymentDto.java            # PaymentRequest / PaymentResponse
    │   ├── PaymentMapper.java
    │   ├── PaymentRepository.java
    │   ├── PaymentService.java
    │   └── PaymentController.java     # REST /api/v1/payments
    └── flouci/
        ├── FlouciPaymentRequest.java  # DTO envoyé à l'API Flouci
        ├── FlouciPaymentResponse.java # Réponse Flouci (payment_url, payment_id)
        ├── FlouciVerifyResponse.java  # Réponse vérification (SUCCESS / FAILED)
        └── FlouciClient.java          # RestTemplate → API Flouci
```

**Fonctionnalités :**
- Initier un paiement via l'API Flouci (retourne un `payment_url`)
- Gérer les callbacks de succès et d'échec (redirections Flouci)
- Vérifier le statut d'un paiement auprès de Flouci
- Sauvegarder l'historique des paiements en base
- Retourner le résultat au BookingService via Feign

**Flux Flouci :**
```
BookingService → POST /api/v1/payments
                       ↓
               FlouciClient.initiatePayment()
                       ↓
               Flouci API → payment_url
                       ↓
               Client redirigé vers Flouci
                       ↓
               Flouci → GET /payments/success?payment_id=xxx
                       ↓
               FlouciClient.verifyPayment(paymentId)
                       ↓
               Payment confirmé → BookingService notifié
```

**Endpoints :**

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/payments` | Initier un paiement |
| `GET` | `/api/v1/payments/success` | Callback succès Flouci |
| `GET` | `/api/v1/payments/fail` | Callback échec Flouci |
| `GET` | `/api/v1/payments/{id}` | Détail d'un paiement |

---

### 📧 notification-service — Port 8040

Consomme les événements Kafka et envoie les emails de confirmation via JavaMailSender et Thymeleaf.

```
notification/
└── src/main/java/com/bookingapp/notification/
    ├── notification/
    │   ├── Notification.java          # Document MongoDB
    │   ├── NotificationDto.java
    │   ├── NotificationMapper.java
    │   ├── NotificationRepository.java  # MongoRepository
    │   ├── NotificationService.java
    │   └── NotificationController.java  # REST /api/v1/notifications
    ├── email/
    │   ├── EmailService.java          # JavaMailSender + Thymeleaf
    │   └── EmailTemplates.java        # Enum des templates disponibles
    └── kafka/
        └── NotificationConsumer.java  # @KafkaListener → "booking-topic"
```

**Fonctionnalités :**
- Écouter le topic Kafka `booking-topic` (réservation confirmée)
- Générer l'email HTML via un template Thymeleaf
- Envoyer l'email au client via SMTP Gmail
- Sauvegarder chaque notification envoyée dans MongoDB
- Consulter l'historique des notifications par client

**Templates email gérés :**

| Template | Déclencheur |
|---|---|
| `booking-confirmation.html` | Réservation confirmée + paiement reçu |
| `booking-cancellation.html` | Réservation annulée |
| `payment-failed.html` | Échec du paiement |

**Endpoints :**

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/notifications/customer/{id}` | Historique notifications client |

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
