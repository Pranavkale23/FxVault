# FxVault: Multi-Currency Payment Engine

FxVault is a robust multi-currency payment engine built with Spring Boot and Angular. It allows for secure and efficient handling of financial transactions across various currencies, real-time exchange rate updates, and reliable event-driven webhooks.

## 🌟 Key Features
- **Multi-Currency Wallets**: Users can hold multiple currencies in a single account.
- **Foreign Exchange (FX)**: Real-time currency conversion using OpenExchangeRates API.
- **Transfers & Transactions**: Send money seamlessly between accounts.
- **Event-Driven Architecture**: Uses Apache Kafka for asynchronous transaction processing and email receipts.
- **Webhooks**: Register endpoints and receive real-time webhook notifications for payment events.
- **Security**: JWT-based authentication and Spring Security.
- **High Performance**: Redis caching for FX rates to minimize external API calls.

## 🛠️ Technology Stack
### Backend
- **Java 17+** & **Spring Boot 3**
- **Spring Data JPA** & **Hibernate**
- **Spring Security** (JWT Authentication)
- **Apache Kafka** (Event Messaging)
- **Redis** (Caching FX Rates)
- **MySQL** (Primary Database)

### Frontend
- **Angular 18** (Standalone Components)
- **TypeScript**
- **RxJS**

## 🚀 Getting Started

### Prerequisites
- [Java 17+](https://adoptium.net/)
- [Node.js & npm](https://nodejs.org/)
- [Docker & Docker Compose](https://www.docker.com/)

### 1. Start Infrastructure Services
Run the following command to spin up MySQL, Redis, Zookeeper, and Kafka using Docker Compose:
```bash
docker-compose up -d
```

### 2. Configure the Application
In `src/main/resources/application.yml`, ensure you have a valid OpenExchangeRates API key if you plan to test live FX conversions:
```yaml
fx:
  api:
    key: YOUR_OPEN_EXCHANGE_RATES_KEY
```

### 3. Start the Backend (Spring Boot)
```bash
./mvnw spring-boot:run
```
The backend API will be available at `http://localhost:8080`.

### 4. Start the Frontend (Angular)
```bash
cd frontend
npm install
npm start
```
The frontend UI will be available at `http://localhost:4200`.

## 📡 API Endpoints Overview
The application exposes several RESTful endpoints:
- **`POST /api/auth/register`**: Register a new user account.
- **`POST /api/auth/login`**: Authenticate and retrieve a JWT token.
- **`GET /api/wallets`**: Fetch all wallets for the authenticated user.
- **`POST /api/transfers`**: Initiate a multi-currency transfer.
- **`GET /api/transactions`**: View transaction history.
- **`POST /api/webhooks`**: Register a webhook URL for transaction events.

## 🏗️ Architecture Diagram
1. **Client** (Angular) -> **API** (Spring Boot)
2. **API** reads/writes to **MySQL** database.
3. **API** caches FX rates in **Redis**.
4. **API** publishes transaction events to **Kafka** topics.
5. Internal listeners consume **Kafka** messages to send Webhooks and generate Email receipts.

## 📄 License
This project is licensed under the MIT License.
