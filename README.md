# MovieBooker - Online Movie Ticket Booking System

## Introduction
MovieBooker is an online movie ticket booking system that allows users to register, log in, book tickets, and manage their booking history. The system leverages **Spring Boot**, **JWT Authentication**, **Kafka**, **WebSocket**, and **MySQL** to ensure high performance and scalability.


## Key Features
### User Features
- Register / Login (JWT Authentication)
- Select movies, showtimes, and seats
- View booking history
- Receive real-time booking confirmation via **WebSocket**

### Admin Features
- Manage movies and showtimes
- Manage cinemas and screening rooms
- Monitor revenue and ticket sales statistics

### System Features
- Handle concurrent seat booking with **Apache Kafka**
- Synchronize data between modules using **Kafka Events**
- Manage database versions with **Flyway**
- Cache movie and showtime data with **Redis**
- Send real-time booking confirmation via **WebSocket**

## Technologies Used
| Technology | Description |
|------------|--------------------------------|
| **Java 21** | Main programming language |
| **Spring Boot** | Backend framework |
| **Spring Security + JWT** | Authentication & authorization |
| **MySQL** | Main database |
| **Flyway** | Database migration management |
| **Apache Kafka** | Concurrent seat booking handling |
| **Redis** | Caching for movies and showtimes |
| **Lombok** | Reduces boilerplate code |
| **Swagger OpenAPI** | API documentation |
| **WebSocket** | Real-time booking confirmation |

##  Installation & Running the Project
### 1 Requirements
- Java 21
- Maven
- MySQL
- Docker (optional, if running Kafka via Docker)

### 2 Configure MySQL
Create a database and update `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/movie_booker
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
```

### 3 Run Kafka (Docker)
```sh
docker-compose up -d
```

### 4 Start the Application
```sh
mvn spring-boot:run
```

### 5 Access the API
- Authentication API: `/api/auth/login`, `/api/auth/register`
- Booking API: `/api/booking`

### 6 Websocket Configuration
- Endpoint: `ws://localhost:8080/ws`
- Client Subscription: `/topic/booking`

## Notes
- This is a demo project; additional features such as **Email confirmation**, **E-wallet integration**, and **Realtime notifications** can be added.

