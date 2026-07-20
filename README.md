# FxVault

FxVault is a robust multi-currency payment engine built with Spring Boot and Angular. It allows for secure and efficient handling of financial transactions across various currencies.

## Features
- **Multi-Currency Support**: Handle transactions in multiple currencies seamlessly.
- **Spring Boot Backend**: A powerful backend architecture using Java, Spring Boot, Spring Data JPA, and Spring Security.
- **Angular Frontend**: A responsive, modern frontend built with Angular.
- **Microservices-Ready**: Integrates Kafka for messaging and Zookeeper for coordination.
- **Caching**: Utilizes Redis for fast data caching.
- **Database**: Relies on MySQL for robust and persistent data storage.

## Prerequisites
- Java 17+
- Node.js & npm
- Docker & Docker Compose

## Running the Application
1. Start the supporting services using Docker Compose:
   ```bash
   docker-compose up -d
   ```
2. Start the Backend:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Start the Frontend:
   ```bash
   cd frontend
   npm install
   npm start
   ```

## Architecture
- Backend runs on `http://localhost:8080`
- Frontend runs on `http://localhost:4200`
- MySQL, Redis, Zookeeper, and Kafka run inside Docker containers.
