#  BookingApp - Microservices Spring Boot

> Application de réservation d'hôtels basée sur une architecture microservices avec Spring Boot, Spring Cloud, Kafka, Flouci et JavaMa.

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

- **Java 21** / Spring Boot 3.x
- **Spring Cloud** : Config Server, Eureka, Gateway, OpenFeign
- **Apache Kafka** — messaging asynchrone entre services
- **OpenFeign** — communication HTTP synchrone inter-services
- **Flouci API** — paiement en ligne (Tunisie)
- **JavaMailSender + Thymeleaf** — emails transactionnels
- **Docker & Docker Compose** — containerisation
- **PostgreSQL** — base de données des services métier
- **MongoDB** — stockage des notifications

---

## Approche par feature

Chaque microservice est organisé **par feature**. Chaque dossier de feature regroupe ses propres couches :

```
feature/
├── Entity.java
├── EntityDto.java
├── EntityMapper.java
├── EntityRepository.java
├── EntityService.java
└── EntityController.java
```

---

## Fonctionnalités par service

### ⚙️ config-server
Centralise toutes les configurations des microservices. Chaque service récupère sa configuration au démarrage via Spring Cloud Config.

### 🔍 discovery
Registre Eureka permettant aux microservices de s'enregistrer et de se découvrir mutuellement sans adresses en dur.

### 🌐 gateway
Point d'entrée unique qui route les requêtes vers les microservices via Eureka, et gère les filtres globaux (CORS, sécurité, logging).

### 👤 customer-service
Gestion du cycle de vie des clients : création, mise à jour, consultation et vérification d'existence. Exposé aux autres services via Feign.

### 🏨 hotel-service
Gestion des hôtels et de leurs chambres : CRUD complet, vérification de disponibilité et mise à jour du statut des chambres (disponible / réservée).

### 📅 booking-service
Orchestration du processus de réservation : vérification du client et de la disponibilité via Feign, création de la réservation, déclenchement du paiement et publication de l'événement de confirmation sur Kafka.

### 💳 payment-service
Traitement des paiements via l'API Flouci : initiation du paiement, gestion des callbacks de succès et d'échec, vérification du statut et historique des transactions.

### 📧 notification-service
Consommation des événements Kafka et envoi d'emails HTML transactionnels au client via JavaMailSender et des templates Thymeleaf. Historique des notifications stocké dans MongoDB.

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
