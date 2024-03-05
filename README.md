# Bank Microservices

[![Java Version](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.java.net/)
[![Spring Boot Version](https://img.shields.io/badge/Spring%20Boot-3.2.3-green.svg)](https://spring.io/projects/spring-boot)

This repository contains a set of microservices for a bank application, implemented using Java Spring Boot.

## Components

### 1. Users Service

The Users service manages user-related operations such as user registration, authentication, and profile management.

### 2. Wallet Service

The Wallet service handles operations related to user wallets, including balance management and transaction history.

### 3. Transactions Service

The Transactions service is responsible for executing financial transactions between user wallets.

### 4. Gateway

The Gateway service serves as an entry point to the microservices architecture, providing routing, filtering, and load balancing functionalities.

### 5. Eureka Server

The Eureka server implements the service registry and discovery for the microservices. It allows services to locate and communicate with each other without hard-coded URLs.

## Setup

### Prerequisites

- Java Development Kit (JDK) 21
- Apache Maven
- Git

### Installation

1. Clone the repository:

    ```bash
   git clone https://github.com/twincie/BankMicroservices.git
    ```

2. Navigate to the project directory:

    ```bash
    cd BankMicroservices
    ```

### Usage

Once all services are up and running, you can access them via their respective endpoints:

- Users Service: http://localhost:8081
- Wallet Service: http://localhost:8082
- Transactions Service: http://localhost:8083
- Gateway: http://localhost:8084
- Eureka Dashboard: http://localhost:8761

## Contributing

Contributions are welcome! Please fork the repository and create a pull request for any enhancements or bug fixes.
