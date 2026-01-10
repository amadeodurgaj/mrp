# Media Ratings Platform (MRP)

Important: If you are trying to start this app copy the dist files, and read the info/DBCONFIG.txt to replace the data.
Other steps are shown in the Running Application section.

The Media Ratings Platform (MRP) is a standalone Java application that implements a RESTful HTTP API for managing users, media entries, ratings, and favorites. It is designed to serve as the backend for potential web or mobile frontends.

---

## Overview

MRP allows users to:
- Register and log in using unique credentials
- View and update their user profile
- Create, update, and delete media entries
- Rate media entries (1–5 stars) with optional comments
- Edit or delete their own ratings
- Like other users’ ratings
- Mark and unmark media entries as favorites
- View personal rating history and favorite lists
- Receive recommendations based on rating behavior and content similarity

The server is built using Java’s built-in HTTP server  
(`com.sun.net.httpserver.HttpServer`) and uses **PostgreSQL** for persistent storage.

---

## Features

- RESTful API following HTTP specifications
- Token-based authentication using Bearer tokens
- Centralized authorization via `AuthUtil`
- Strict Controller–Service–Repository architecture
- Custom exception hierarchy with proper HTTP status mapping
- Centralized HTTP method validation (`HttpMethodValidator`)
- Middleware-style request processing (authentication, logging)
- Ownership enforcement for media and ratings
- Duplicate-safe behavior for ratings and favorites
- UUID-based user IDs for scalable and distributed compatibility
- Unit tests covering core business logic
- Official Postman collection for manual and automated testing

---

## Database Schema

| Table Name       | Description |
|------------------|-------------|
| **users**        | Stores registered users (UUID primary key, credentials, profile data) |
| **media**        | Media entries (movies, series, games) created by users |
| **media_genres** | Many-to-many relationship between media and genres |
| **ratings**      | User ratings (1–5 stars) with optional comments and moderation flag |
| **rating_likes** | Tracks which users liked which ratings |
| **favorites**    | Maps users to their favorite media entries |

---

## Running the Application

### 1. Prerequisites

- Java 24
- PostgreSQL 14+ (local or Docker)
- Maven

---

### 2. Database Setup

1. Copy the `docker-compose.yml.dist`
2. Create `info/DBCONFIG.txt`
3. Insert the database credentials as described in the file
4. Ensure the file is ignored by Git

---

### 3. Start the Server

Start it by running the main App:
`org.mrp.App`

The server will start at:

```
http://localhost:8080
```

---

## API Endpoints (Excerpt)

| Method | Endpoint                  | Description                         |
|--------|---------------------------|-------------------------------------|
| POST   | /api/users/register       | Register a new user                 |
| POST   | /api/users/login          | Log in and receive a token          |
| GET    | /api/users/{id}/profile   | Retrieve user profile               |
| PUT    | /api/users/{id}/profile   | Update user profile                 |
| GET    | /api/users/{id}/ratings   | Retrieve user rating history        |
| GET    | /api/users/{id}/favorites | Retrieve user favorites             |
| POST   | /api/media                | Create a media entry                |
| GET    | /api/media                | Get all media entries               |
| GET    | /api/media/{id}           | Retrieve a media entry              |
| PUT    | /api/media/{id}           | Update a media entry (creator only) |
| DELETE | /api/media/{id}           | Delete a media entry (creator only) |
| POST   | /api/ratings/media/{id}   | Rate a media entry                  |
| PUT    | /api/ratings/{id}         | Update media rating                 |
| DELETE | /api/ratings/{id}         | Delete media rating                 |
| POST   | /api/media/{id}/favorite  | Mark media as favorite              |
| DELETE | /api/media/{id}/favorite  | Remove media from favorites         |
| POST   | /api/ratings/{id}/like    | Like a rating                       |
| POST   | /api/ratings/{id}/confirm | Confirm rating comment              |

A complete and tested Postman collection is provided in  
`MRP_Postman_Collection.json`.

---

## Error Handling

All application errors extend `ApiException`, which includes an HTTP status code.

Errors are returned as JSON:

```json
{ "error": "User 'john' already exists" }
```

Common exception mappings:

- `BadRequestException` → 400
- `InvalidCredentialsException` → 401
- `ForbiddenAccessException` → 403
- `UserAlreadyExistsException` → 409
- `InternalServerException` → 500

---

## Testing

Unit tests are written using **JUnit 5** and **Mockito** and focus on:

- Authorization and ownership validation
- Rating uniqueness constraints
- Favorite add/remove behavior
- Business rule enforcement
- Exception handling paths

A Postman collection is included for manual and automated API testing.


## License

This project is developed for academic purposes under the Software Engineering 1 course at FH Technikum Wien.
