# MovieBooker - Online Movie Ticket Booking System

## 🚀 Introduction
MovieBooker is an online movie ticket booking system that allows users to register, log in, book tickets, make payments, and manage their booking history. The project utilizes **Spring Boot**, **JWT Authentication**, **Kafka**, and **MySQL** to ensure performance and scalability.

## 🎯 Key Features
### 🔹 User Features
- Register / Login (JWT Authentication)
- Select movies, showtimes, and seats
- Make payments for movie tickets (Payment gateway integration)
- View booking history

### 🔹 Admin Features
- Manage movies and showtimes
- Manage cinemas and screening rooms
- Monitor revenue and ticket sales statistics

### 🔹 System Features
- Handle concurrent seat booking with **Apache Kafka**
- Database version management with **Flyway**
- Authentication and authorization with **JWT**

## 🛠️ Technologies Used
| Technology | Description |
|------------|--------------------------------|
| **Java 21** | Main programming language |
| **Spring Boot** | Backend framework |
| **Spring Security + JWT** | Authentication & authorization |
| **MySQL** | Main database |
| **Flyway** | Database migration management |
| **Apache Kafka** | Handling concurrent seat booking |
| **Lombok** | Simplifies coding |
| **Swagger OpenAPI** | API documentation |

## ⚙️ Installation & Running the Project
### 1️⃣ Requirements
- Java 21
- Maven
- MySQL
- Docker (optional, if running Kafka via Docker)

### 2️⃣ Configure MySQL
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

### 3️⃣ Run Kafka (Docker)
```sh
docker-compose up -d
```

### 4️⃣ Start the Application
```sh
mvn spring-boot:run
```

### 5️⃣ Access the API
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Authentication API: `/api/auth/login`, `/api/auth/register`
- Booking API: `/api/booking`

## 📌 Notes
- This is a demo project; additional features such as **Email confirmation**, **E-wallet integration**, and **Realtime notifications** can be added.

