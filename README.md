# NammaArena - Booking Service

Booking Management Service for the NammaArena microservices architecture. Handles turf bookings for users and booking management for owners.

## Tech Stack
- Java 17
- Spring Boot 3.2.0
- Spring Security
- JWT (jjwt 0.11.5)
- Spring Data JPA
- Spring Cloud OpenFeign
- MySQL

## Port
`8083`

## Getting Started

### Prerequisites
- Java 17
- Maven 3.9+
- MySQL running on port 3306
- Eureka Server running on port 8761

### Run Locally
```bash
mvn clean package -DskipTests
java -jar target/*.jar
```

### Run with Docker
```bash
docker build -t nammaarena-booking-service .
docker run -p 8083:8083 nammaarena-booking-service
```

## API Endpoints
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/bookings` | Create a new booking | User only |
| GET | `/api/bookings/my` | Get my bookings | User only |
| GET | `/api/bookings/owner` | Get bookings for owner's turfs | Owner only |
| PUT | `/api/bookings/{id}/cancel` | Cancel a booking | User only |

## Part of NammaArena Microservices
| Service | Repository |
|---|---|
| Eureka Server | [NammaArena-EurekaServer](https://github.com/VamsiK-11/NammaArena-EurekaServer) |
| API Gateway | [NammaArena-APIGateway](https://github.com/VamsiK-11/NammaArena-APIGateway) |
| Auth Service | [NammaArena-AuthService](https://github.com/VamsiK-11/NammaArena-AuthService) |
| Turf Service | [NammaArena-TurfService](https://github.com/VamsiK-11/NammaArena-TurfService) |
| Booking Service | [NammaArena-BookingService](https://github.com/VamsiK-11/NammaArena-BookingService) |
| Payment Service | [NammaArena-PaymentService](https://github.com/VamsiK-11/NammaArena-PaymentService) |
