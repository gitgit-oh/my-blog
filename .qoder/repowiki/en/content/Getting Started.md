# Getting Started

<cite>
**Referenced Files in This Document**
- [application.yml](file://blog-backend/src/main/resources/application.yml)
- [pom.xml](file://blog-backend/pom.xml)
- [BlogApplication.java](file://blog-backend/src/main/java/com/blog/BlogApplication.java)
- [schema.sql](file://blog-backend/src/main/resources/schema.sql)
- [data.sql](file://blog-backend/src/main/resources/data.sql)
- [DataInitializer.java](file://blog-backend/src/main/java/com/blog/config/DataInitializer.java)
- [AdminService.java](file://blog-backend/src/main/java/com/blog/service/AdminService.java)
- [AdminController.java](file://blog-backend/src/main/java/com/blog/controller/AdminController.java)
- [JwtInterceptor.java](file://blog-backend/src/main/java/com/blog/config/JwtInterceptor.java)
- [RedisConfig.java](file://blog-backend/src/main/java/com/blog/config/RedisConfig.java)
- [WebConfig.java](file://blog-backend/src/main/java/com/blog/config/WebConfig.java)
- [vite.config.js](file://blog-frontend/vite.config.js)
- [package.json](file://blog-frontend/package.json)
- [request.js](file://blog-frontend/src/api/request.js)
- [auth.js](file://blog-frontend/src/stores/auth.js)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Installation Overview](#installation-overview)
4. [Step-by-Step Installation](#step-by-step-installation)
5. [Database Setup](#database-setup)
6. [Environment Configuration](#environment-configuration)
7. [Running the Applications](#running-the-applications)
8. [Development Server Startup](#development-server-startup)
9. [Port Configurations](#port-configurations)
10. [Initial Admin Account Setup](#initial-admin-account-setup)
11. [Verification Steps](#verification-steps)
12. [Troubleshooting Guide](#troubleshooting-guide)
13. [Conclusion](#conclusion)

## Introduction
This guide helps you install and run the my-Blob blog system locally. It covers prerequisites, database setup, environment configuration, and launching both the backend (Spring Boot) and frontend (Vue + Vite) applications. You will also learn how to verify your installation and resolve common setup issues.

## Prerequisites
Before installing my-Blob, ensure you have the following installed on your system:
- Java 17 or higher (required by the Spring Boot backend)
- Node.js (for building and running the Vue frontend)
- MySQL (for storing blog data)
- Redis (for caching and session-related features)
- Elasticsearch (for search functionality)

These requirements are reflected in the backend configuration and dependencies.

**Section sources**
- [pom.xml:21-23](file://blog-backend/pom.xml#L21-L23)
- [application.yml:4-19](file://blog-backend/src/main/resources/application.yml#L4-L19)

## Installation Overview
The my-Blob system consists of:
- Backend: Spring Boot application with MyBatis, MySQL, Redis, and Elasticsearch integrations
- Frontend: Vue 3 application built with Vite, communicating with the backend via HTTP requests

The backend exposes REST endpoints under `/api`, while the frontend proxies these requests through Vite's development server.

**Section sources**
- [BlogApplication.java:8-10](file://blog-backend/src/main/java/com/blog/BlogApplication.java#L8-L10)
- [vite.config.js:9-18](file://blog-frontend/vite.config.js#L9-L18)
- [request.js:4-7](file://blog-frontend/src/api/request.js#L4-L7)

## Step-by-Step Installation
Follow these steps to prepare your environment and launch the system:

1. **Install Prerequisites**
   - Install Java 17+ and verify with your system's Java runtime
   - Install Node.js and verify with your system's Node runtime
   - Install and start MySQL, Redis, and Elasticsearch services

2. **Clone or Prepare the Repository**
   - Place the repository root at a convenient location on your machine

3. **Backend Setup**
   - Navigate to the backend directory
   - Build the backend using Maven (this creates the Spring Boot executable)
   - Confirm the backend runs on port 8080

4. **Frontend Setup**
   - Navigate to the frontend directory
   - Install dependencies using your preferred package manager
   - Start the frontend development server

5. **Database Initialization**
   - Create the MySQL database and run the schema script
   - Optionally seed initial data if needed

6. **Environment Variables**
   - Configure connection strings for MySQL, Redis, and Elasticsearch in the backend configuration file

7. **Launch Both Applications**
   - Start the backend first, then the frontend
   - Access the frontend at the configured port and log in using the admin credentials

**Section sources**
- [pom.xml:94-109](file://blog-backend/pom.xml#L94-L109)
- [BlogApplication.java:12-14](file://blog-backend/src/main/java/com/blog/BlogApplication.java#L12-L14)
- [vite.config.js:6-19](file://blog-frontend/vite.config.js#L6-L19)

## Database Setup
The backend expects a MySQL database with the following schema and optional initial data.

- Database name: configured in the backend configuration
- Schema creation SQL is provided in the repository
- Initial admin account data is provided in the repository

Steps:
1. Create a MySQL database named according to your backend configuration
2. Run the schema script to create tables
3. Optionally run the data script to seed the admin account

Tables created by the schema include:
- category
- outline
- article
- admin

**Section sources**
- [application.yml:6-8](file://blog-backend/src/main/resources/application.yml#L6-L8)
- [schema.sql:1-33](file://blog-backend/src/main/resources/schema.sql#L1-L33)
- [data.sql:1-2](file://blog-backend/src/main/resources/data.sql#L1-L2)

## Environment Configuration
Configure the backend environment by editing the application configuration file. Key areas to review:
- Server port (default 8080)
- MySQL datasource URL, username, and password
- Redis host, port, and database index
- Elasticsearch URI
- JWT secret and expiration
- Upload path for media files

Ensure these match your local services' addresses and credentials.

**Section sources**
- [application.yml:1-33](file://blog-backend/src/main/resources/application.yml#L1-L33)

## Running the Applications
Start the backend and frontend applications in sequence:

Backend (Spring Boot):
- Build the backend using Maven
- Run the Spring Boot application
- The application scans for mappers and enables caching

Frontend (Vue + Vite):
- Install dependencies
- Start the development server
- The frontend proxies API calls to the backend

**Section sources**
- [BlogApplication.java:8-10](file://blog-backend/src/main/java/com/blog/BlogApplication.java#L8-L10)
- [package.json:6-10](file://blog-frontend/package.json#L6-L10)

## Development Server Startup
The frontend development server is configured with:
- Port 5173
- Host binding enabled
- Proxy rules for `/api` and `/upload` traffic to the backend

To start:
- From the frontend directory, run the development script
- Open your browser to the configured port

The backend development server listens on port 8080 by default.

**Section sources**
- [vite.config.js:6-19](file://blog-frontend/vite.config.js#L6-L19)
- [application.yml:1-2](file://blog-backend/src/main/resources/application.yml#L1-L2)

## Port Configurations
- Backend HTTP port: 8080
- Frontend development port: 5173
- Backend also uses ports for Redis (default 6379) and Elasticsearch (default 9200)

Ensure these ports are free or adjust the configuration accordingly.

**Section sources**
- [application.yml:2](file://blog-backend/src/main/resources/application.yml#L2)
- [application.yml:15-19](file://blog-backend/src/main/resources/application.yml#L15-L19)
- [vite.config.js:7](file://blog-frontend/vite.config.js#L7)

## Initial Admin Account Setup
There are two ways to initialize the admin account:

Option A: Seed data
- The repository includes a data script that inserts an admin user with a predefined username and password hash
- Apply this script after creating the database schema

Option B: Automatic initialization
- On application startup, a data initializer checks for the existence of an admin user
- If not found, it creates an admin user with a default password
- This ensures the admin account exists even without seeding

After successful initialization, you can log in to the admin panel using the configured credentials.

**Section sources**
- [data.sql:1-2](file://blog-backend/src/main/resources/data.sql#L1-L2)
- [DataInitializer.java:14-17](file://blog-backend/src/main/java/com/blog/config/DataInitializer.java#L14-L17)
- [AdminService.java:24-32](file://blog-backend/src/main/java/com/blog/service/AdminService.java#L24-L32)

## Verification Steps
To confirm a successful installation:

1. Backend Health
   - Verify the backend starts without errors
   - Confirm the server port is listening

2. Database Connectivity
   - Ensure the backend connects to MySQL using the configured credentials
   - Confirm the schema tables exist

3. Redis and Elasticsearch
   - Confirm Redis and Elasticsearch are reachable at the configured URIs

4. Frontend Access
   - Open the frontend in a browser
   - Verify the admin login page loads
   - Log in using the admin credentials

5. API Communication
   - Confirm the frontend can communicate with backend endpoints via the proxy
   - Check that authentication tokens are handled by the frontend store

**Section sources**
- [BlogApplication.java:12-14](file://blog-backend/src/main/java/com/blog/BlogApplication.java#L12-L14)
- [application.yml:4-19](file://blog-backend/src/main/resources/application.yml#L4-L19)
- [request.js:4-7](file://blog-frontend/src/api/request.js#L4-L7)
- [auth.js:5](file://blog-frontend/src/stores/auth.js#L5)

## Troubleshooting Guide
Common setup issues and resolutions:

- Backend fails to start due to Java version
  - Ensure Java 17+ is installed and selected by your environment

- MySQL connection errors
  - Verify the database name, username, and password in the backend configuration
  - Confirm the MySQL service is running and accessible

- Redis or Elasticsearch connectivity issues
  - Check that Redis and Elasticsearch are running on the configured ports
  - Adjust the URIs in the backend configuration if services are hosted elsewhere

- Frontend cannot reach backend during development
  - Confirm the proxy configuration in the frontend matches the backend port
  - Ensure the backend is running before starting the frontend

- Admin login fails
  - Use the credentials from the data seed or the automatically created admin account
  - If the password was auto-generated, reset it through the admin interface if available

- Port conflicts
  - Change the backend or frontend port in their respective configuration files
  - Free up the conflicting port or adjust the configuration

**Section sources**
- [pom.xml:21-23](file://blog-backend/pom.xml#L21-L23)
- [application.yml:6-8](file://blog-backend/src/main/resources/application.yml#L6-L8)
- [application.yml:15-19](file://blog-backend/src/main/resources/application.yml#L15-L19)
- [vite.config.js:9-18](file://blog-frontend/vite.config.js#L9-L18)

## Conclusion
You now have the necessary steps to install, configure, and run the my-Blob blog system locally. By following the prerequisites, database setup, environment configuration, and development server startup procedures, you can launch both the backend and frontend applications and access the admin panel. Use the verification and troubleshooting sections to ensure everything is working correctly and to resolve common issues quickly.