# Media Ratings Platform (MRP)

Important: If you are trying to start this app copy the dist files, and read the info/DBCONFIG.txt to replace the data.
Other steps are shown in the Running Application section.

The Media Ratings Platform (MRP) is a standalone Java application that implements a RESTful HTTP API for managing users, media entries, ratings, and favorites. It is designed to serve as the backend for potential web or mobile frontends.

## Overview

MRP allows users to:
- Register and log in using unique credentials
- View and update their profiles
- Create, update, delete, and rate media entries
- Like other users' ratings
- Mark media as favorites
- Retrieve recommendations based on rating history

The server is built using the built-in Java HTTP server (com.sun.net.httpserver.HttpServer) and connects to a PostgreSQL database for persistent storage.

## Features

- RESTful API endpoints following HTTP specifications
- Token-based user authentication
- Modular routing via dedicated router classes
- Custom exception hierarchy for clean error handling
- Centralized HTTP method validation (HttpMethodValidator)
- Middleware system supporting logging and authentication
- UUID-based user IDs for scalable, distributed compatibility
- Integration tests using JUnit and the official Postman collection


## Database Schema

| Table Name   | Description |
|---------------|-------------|
| **users** | Stores registered users (UUID primary key, login credentials, profile info) |
| **media** | Represents media entries (movies, series, games) created by users |
| **media_genres** | Many-to-many link between media and genres |
| **ratings** | Contains user ratings (1–5 stars) and optional comments for media |
| **rating_likes** | Tracks which users liked which ratings |
| **favorites** | Maps users to their favorite media entries |

## Running the Application

### 1. Prerequisites
- Java 24
- PostgreSQL 14+ (local or Docker)
- Maven

### 2. Database Setup

- Copy the docker-compose.yml.dist
- Add the data given to you in the info/DBCONFIG.txt which should be ignored by git.

### 3. Start the Server

mvn compile exec:java -Dexec.mainClass="org.mrp.App"

Server runs at: http://localhost:8080

## API Endpoints

| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | /api/users/register | Register a new user |
| POST | /api/users/login | Log in and receive a token |
| GET | /api/users/{username}/profile | Retrieve user profile |
| PUT | /api/users/{username}/profile | Update user profile |


## Error Handling

All exceptions extend ApiException, which carries an HTTP status code.
Responses are automatically formatted as JSON, for example:
{ "error": "User 'john' already exists" }

Common error types:
- BadRequestException → 400
- InvalidCredentialsException → 401
- UserAlreadyExistsException → 409
- InternalServerException → 500

## Integration Tests

Integration tests are written using JUnit 5 and use the Java HttpClient API to hit live endpoints.

Run all tests:

mvn test

Example test: UserIntegrationTest covers registration, login, duplicate detection, profile retrieval, and profile update.

A Postman collection (MRP_Postman_Collection.json) is also included for manual and automated API testing.

## Future Extensions

- Media CRUD endpoints (/api/media)
- Rating and comments system
- Favorites and recommendations
- Leaderboard of most active users
- Docker Compose setup for full local environment
- JSON error middleware and request timing metrics

## License

This project is developed for academic purposes under the Software Engineering 1 course at FH Technikum Wien.
