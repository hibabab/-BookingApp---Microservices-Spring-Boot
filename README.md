#  BookingApp - Microservices Spring Boot

> Application de réservation d'hôtels basée sur une architecture microservices avec Spring Boot, Spring Cloud, Kafka, Flouci et JavaMail.

---


## Microservices

| Service | Port | Rôle | Base de données |
|---|---|---|---|
| `config-server` | 8888 | Centralisation de la configuration | Git / Filesystem |
| `discovery` | 8761 | Registre Eureka (Service Discovery) | — |
| `gateway` | 8222| Point d'entrée unique (Spring Cloud Gateway) | — |
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
