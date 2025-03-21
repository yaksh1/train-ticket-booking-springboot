# Train Ticket Booking Application

## 📋 Overview

A comprehensive train ticket booking platform built with Spring Boot backend and React frontend, designed for Indian railway travelers. The system provides end-to-end functionality for ticket booking, user management, and train administration.

## 🚀 Core Features

### 👤 User Management

- User registration and authentication
- Secure login system
- Session tracking and management

### 🎫 Ticket Operations

- Train search functionality
  - Filter by source, destination, and date
  - Real-time availability checking
- Ticket booking with multiple seat selection
- Digital ticket management
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

## 🏗 Technical Architecture

### Backend Infrastructure

The application is built on a robust Spring Boot foundation with RESTful architecture.

**Core Technologies:**
- Spring Boot (Backend Framework)
- Spring Data JPA (ORM Layer)
- MySQL (Database)
- Spring Security (Authentication)
- RestTemplate (Service Communication)

### API Documentation

#### User Endpoints

| Endpoint | Method | Purpose |
|----------|---------|---------|
| `/v1/user/signupUser` | POST | Create new user account |
| `/v1/user/loginUser` | POST | Authenticate user |
| `/v1/user/bookTicket` | POST | Create ticket reservation |
| `/v1/user/fetchTickets` | GET | Retrieve user bookings |
| `/v1/user/cancelTicket` | POST | Cancel reservation |
| `/v1/user/rescheduleTicket` | POST | Modify travel date |

#### Train Management Endpoints

| Endpoint | Method | Purpose |
|----------|---------|---------|
| `/v1/train/searchTrains` | GET | Find available trains |
| `/v1/train/addTrain` | POST | Create train entry |
| `/v1/train/updateTrain` | POST | Modify train details |
| `/v1/train/addMultipleTrains` | POST | Bulk train creation |

## 🔄 Booking Workflow

1. Train Search
   - User inputs travel criteria
   - System queries available trains

2. Seat Selection
   - Available seat display
   - Multiple seat booking support

3. Booking Confirmation
   - Seat availability verification
   - Ticket generation
   - User account association

4. Booking Management
   - Cancellation processing
   - Seat inventory updates
   - Booking modifications

## ⚠️ Error Management

The system implements comprehensive error handling:

- Train availability validation
- Seat inventory checks
- Booking conflict resolution
- System state validation
- User input verification


