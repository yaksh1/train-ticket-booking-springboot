# 🚂 Train Ticket Booking Application

A comprehensive train ticket booking platform built with Spring Boot backend and React frontend, designed for Indian railway travelers. The system provides end-to-end functionality for ticket booking, user management, and train administration.

## 📋 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Architecture](#-architecture)
- [Docker Deployment](#-docker-deployment)
- [Error Handling](#-error-handling)
- [Contributing](#-contributing)

## Features

### 👤 User Management
- User registration and authentication
- Secure login system with email and password
- Session tracking and management
- User profile management

### 🎫 Ticket Operations
- **Train Search**
  - Filter by source, destination, and date
  - Real-time availability checking
- **Booking Management**
  - Multiple seat selection
  - Digital ticket generation
  - View booking history
  - Cancel reservations
  - Reschedule travel dates

### 🚂 Administrative Controls
- Train management system
  - Add new trains
  - Update train information
  - Bulk train data import
- Schedule and capacity management
- Real-time availability updates

## Tech Stack

### Backend
- **Framework**: Spring Boot
- **Database**: MongoDB
- **Security**: Spring Security
- **API**: RESTful architecture
- **Documentation**: JavaDoc

## Getting Started

### Prerequisites
- Java 11 or higher
- Node.js and npm
- Docker and Docker Compose
- MongoDB

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yaksh1/train-ticket-booking-springboot

cd train-ticket-booking-springboot
```

2. Start the MongoDB and application using Docker Compose:
```bash
docker-compose up -d
```

The application will be available at `http://localhost:8080`

## API Documentation

### User Endpoints

| Endpoint | Method | Description |
|----------|---------|-------------|
| `/v1/user/signupUser` | POST | Create new user account |
| `/v1/user/loginUser` | POST | Authenticate user |
| `/v1/user/bookTicket` | POST | Create ticket reservation |
| `/v1/user/fetchTickets` | GET | Retrieve user bookings |
| `/v1/user/cancelTicket` | POST | Cancel reservation |
| `/v1/user/rescheduleTicket` | POST | Modify travel date |

### Train Management Endpoints

| Endpoint | Method | Description |
|----------|---------|-------------|
| `/v1/train/searchTrains` | GET | Find available trains |
| `/v1/train/addTrain` | POST | Create train entry |
| `/v1/train/updateTrain` | POST | Modify train details |
| `/v1/train/addMultipleTrains` | POST | Bulk train creation |

## Architecture

The application follows a monolithic architecture with:
- RESTful API design
- Layered architecture (Controller, Service, Repository)
- Data persistence with MongoDB
- Containerized deployment using Docker

## Docker Deployment

The application is containerized using Docker and can be easily deployed using Docker Compose. The configuration includes:
- MongoDB container for database
- Application container for the Spring Boot service
- Automatic container restart
- Volume persistence for database data
- Internal network for service communication

## Error Management

The system implements comprehensive error handling through:
- Custom exception handling
- Train availability validation
- Seat inventory checks
- Booking conflict resolution
- System state validation
- User input verification

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
