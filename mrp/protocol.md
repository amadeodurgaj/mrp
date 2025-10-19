# Media Ratings Platform (MRP) - Development Protocol

Author: Amadeo Durgaj  
Date: October 2025  
Branch: Test

---

## Development Timeline and Commit Summary

### **Initial Setup (September 17, 2025)**
**Commit:** 2adef194298ea5e91a95683265b7ebf804d31843  
**Message:** initial commit  
**Description:**  
The project repository was initialized. The base structure and Maven configuration were set up to allow further development.

**Commit:** 81030eb24f73a83f92435547c7fa84b9f7789184  
**Message:** DB Connection Tested  
**Description:**  
A database connection to PostgreSQL was established and successfully tested. The first version of `DBUtil` was verified to ensure SQL operations worked properly.

---

### **User Management Implementation (September 30 – October 4, 2025)**

**Commit:** 9cd5a54bab51cb1b1b2cafe404df09d2fe0ab1c1  
**Message:** Tested the controller with Postman for registration  
**Description:**  
Initial HTTP routes for user registration were implemented and tested manually via Postman. Basic JSON handling and database inserts were verified.

**Commit:** 745d973047be5e0697b1aea5a15848d16b76bcda  
**Message:** Created JSONUtil and UserService for SRP compliance  
**Description:**  
Code was refactored to comply with the **Single Responsibility Principle (SRP)**.  
A dedicated `UserService` was created to handle business logic, and a `JSONUtil` class was added to separate JSON serialization and deserialization from controller logic.

**Commit:** a81abc922bbf3d0686c7cddda0389343d201cc67  
**Message:** added media and game models complying to LSP  
**Description:**  
Introduced `Media` and `Game` models and structured them under an inheritance hierarchy.  
Changes were made to follow the **Liskov Substitution Principle (LSP)**, ensuring that all subclasses could be used interchangeably with their parent class.

**Commit:** e836260f6d5e3f5b0bc8d44e7eaf12209e13bb4e  
**Message:** Change to media for LSP compliance  
**Description:**  
Minor corrections to the media model to fully comply with LSP.  
Validation logic was adjusted to ensure media subclasses behaved consistently.

**Commit:** 50759fc50328e8c10b602396c68a3421256f1d15  
**Message:** Added user functionality for logging in, registering, editing and viewing profile  
**Description:**  
User endpoints were expanded to include login, profile retrieval, and profile editing.  
Authentication handling and password validation were introduced.

**Commit:** 170f852ee622447120f09463f59d4993900e4539  
**Message:** added full user implementation with ratings  
**Description:**  
Completed user integration with the `ratings` system.  
Users can now rate media entries and have personal statistics (total ratings, favorites, etc.) calculated.

---

### **Refactoring and Architectural Compliance (October 18, 2025)**

**Commit:** 5f73b2cb498b19f11d2ba017a1e36039bb171c04  
**Message:** Mass refactoring, addition of exceptions and refactoring to pass requirements  
**Description:**  
A major refactor introduced the full exception hierarchy (`ApiException`, `BadRequestException`, etc.).  
Error handling was standardized to return proper HTTP status codes.  
Static error codes were replaced with typed exceptions.  
The project structure was reorganized to separate concerns between controllers, services, and utilities.

---

### **Testing Phase (October 19, 2025)**

**Commit:** 41aceb8f07a4736cf5bb387f0173c9fc64ee8828  
**Message:** Integration Tests  
**Description:**  
JUnit 5 integration tests were implemented for the main API endpoints.  
Tests covered user registration, duplicate user handling, login, invalid credentials, and profile retrieval.  
The Postman collection was used to validate manual endpoint testing, ensuring endpoint behavior matched project requirements.

---

### **Media CRUD implementation (October 19, 2025)**

**Commit:** cfacc99797d3aa6cbf6c94a8e66301b976f05d1f
**Message:** Added Media CRUD integration  
**Description:**  
The whole logic of the media APIs was given with routing, services to call the SQL commands and the controllers to do the 
heavy business logic. In addition, the corresponding integration tests were also created.

---


## Technical Decisions

- **Language:** Java 24
- **Framework:** Built-in `com.sun.net.httpserver.HttpServer` (no external frameworks)
- **Database:** PostgreSQL with UUID-based user IDs
- **Testing:** JUnit 5 for integration testing + Postman for manual verification
- **Error Handling:** Custom exception system mapping to HTTP response codes
- **Architecture:** Controller–Service–Model separation for SRP compliance
- **Version Control:** Git (branch: `Test`)

---

## Challenges and Solutions

| Challenge | Solution |
|------------|-----------|
| Initial database setup and UUID integration | Switched from `SERIAL` to `UUID` and used `uuid_generate_v4()` |
| Static error handling and duplicated code | Introduced typed exceptions and centralized HTTP response handling |
| Repeated HTTP method checks | Implemented `HttpMethodValidator` utility for clean validation |
| Maintaining clean routing | Moved endpoint definitions from `App.java` into a dedicated `RouteRegistrar` |
| Integration testing issues (server not running) | Added server lifecycle management to ensure tests connect to a live instance |

---

## Final State

At the end of development:
- The server runs successfully at `http://localhost:8080`
- User registration, login, and profile management are functional
- Database schema is stable and normalized
- Integration tests pass successfully
- Code complies with the required software architecture principles

---

## Estimated Time Tracking

| Task                                  | Date | Estimated Hours |
|---------------------------------------|------|-----------------|
| Initial setup and DB connection       | Sep 17 | 4h              |
| User registration & Postman testing   | Sep 30 | 4h              |
| Refactoring for SRP & LSP             | Sep 30 | 2h              |
| User get and edit profile integration | Oct 4 | 4h              |
| Exception system and refactor         | Oct 18 | 3h              |
| Integration tests and documentation   | Oct 19 | 3h              |
| Addition of Media CRUD                | Oct 19 | 3h              |
| **Total**                             |  | **23 hours**    |

