# Share My Recipe API Platform - Project Summary

## Overview

This project implements a modern recipe publishing platform with a secure, well-documented REST API. The platform enables public browsing of recipes, chef sign-up, JWT-protected authoring endpoints, and chef following features.

## Key Components

### 1. Main API Application
A Spring Boot application that provides RESTful endpoints for:
- Chef authentication and registration
- Public recipe browsing with filtering and pagination
- Recipe creation, updating, and publishing (JWT protected)
- Chef following functionality

### 2. Worker Application
A separate Spring Boot application that:
- Consumes messages from RabbitMQ queues
- Processes recipe operations asynchronously
- Handles background tasks like image processing, notifications, etc.

## Features Implemented

### Authentication & Authorization
- JWT-based authentication with short-lived access tokens
- Role-based access control (chef role for recipe authoring)
- Secure password storage with BCrypt hashing

### Recipe Management
- Create recipes with title, summary, ingredients, steps, labels
- Draft/Published workflow
- Update and delete own recipes
- Image upload support (1..n images per recipe)

### Chef Social Features
- Follow/unfollow other chefs
- View recipes from followed chefs
- Follower/following lists

### Public Recipe Browsing
- Filter recipes by keyword, date range, chef
- Pagination with configurable page size
- Detailed recipe information including author details

### Asynchronous Processing
- Recipe operations are queued for background processing
- RabbitMQ integration for message queuing
- Separate worker application for processing tasks

## Technology Stack

- **Framework**: Spring Boot 3.1
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: H2 (dev), PostgreSQL (prod ready)
- **Security**: Spring Security, JWT
- **Messaging**: RabbitMQ
- **Persistence**: Spring Data JPA, Hibernate

## API Endpoints

The API provides comprehensive endpoints for all required functionality:
- Authentication endpoints for signup/signin
- Public recipe browsing with filters
- Chef following management
- Recipe creation, updating, publishing, and deletion
- Recipes from followed chefs

## Project Structure

The project is organized into two main modules:
1. Main API application with all REST endpoints
2. Worker application for background processing

Each module has a clean, layered architecture with:
- Controllers for REST endpoints
- Services for business logic
- Repositories for data access
- Entities for data modeling
- DTOs for data transfer

## Security Considerations

- All recipe authoring endpoints are protected with JWT
- Passwords are securely hashed with BCrypt
- Role-based access control prevents unauthorized actions
- Input validation on all endpoints
- CORS configuration for web client integration

## Scalability Features

- Asynchronous processing for time-consuming operations
- Pagination for efficient data retrieval
- Message queuing for load distribution
- Separate worker application for background tasks

## Deployment Ready

- Configuration via properties files
- Database agnostic (H2 for dev, PostgreSQL for prod)
- Docker-ready with appropriate configurations
- Clear separation of concerns for microservices deployment

## Future Enhancements

This platform provides a solid foundation that can be extended with:
- Image resizing and optimization
- Search functionality with Elasticsearch
- Rating and review system
- Recipe collections and bookmarks
- Admin moderation features
- Mobile API optimizations