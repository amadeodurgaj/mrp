# Media Ratings Platform (MRP) - Development Protocol

Author: Amadeo Durgaj  
Date: January 2026  
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

### Structural Consolidation & Repository Layer (October–November 2025)

**Commit:** 249073591d8c19d57488272d0c634dca568f4d6a
**Message:** Implementation with Repositories for SQL calls

**Description:**  
A dedicated **Repository layer** was introduced to encapsulate all SQL operations.  
This finalized the transition to a clean **Controller → Service → Repository** architecture.

---

**Commit:** 53c366b101c0cf152d52c13a969f5ec328e86774
**Message:** Refactoring to have user and media follow the same injection and logics

**Description:**  
User and Media domains were refactored to follow **identical dependency injection and architectural patterns**:
- Constructor-based injection of repositories
- Unified utility usage (JSON handling, validation, auth)
- Shared exception and response handling

This eliminated architectural inconsistencies between domains and improved overall cohesion.

---

### Authentication & Authorization Hardening (December 2025)

**Commit:** eb24b9f79aa1221a48e1fbdee77a40d7ba5e682b  
**Message:** Added AuthUtil and complete integration of global token control – removed integration tests

**Description:**  
A centralized **token-based authorization system** was fully implemented using `AuthUtil`.

---

### Ratings, Favorites & User-Scoped Listings (January 2026)

**Commit:** 14e12db848a3d64431e680d43bd7dacb9dd20154
**Message:** Added functionalities like Rating, Favorites and listing of favorites / ratings for each user

**Description:**  
The full **rating and favorite system** was implemented.

---

### Media Ownership & Access Control Enforcement (January 2026)

**Commit:** `040e42c59ef8e48c240f683c42302cc1b7c6124b`  
**Message:** Fortified media ownership logic

**Description:**  
Strict **media ownership enforcement** was added:
- Only the creator of a media entry may update or delete it
- Ownership validation occurs in the **service layer**
- Unauthorized actions return proper HTTP 403 responses

This prevents horizontal privilege escalation and ensures rule compliance.

---

### Unit Testing & Stability Improvements (January 2026)

**Commit:** `04ec313323ffec1efae97824569318b95ed7adba`  
**Message:** Added Unit Tests and refactored problems found by unit testing

**Description:**  
A dedicated **unit testing phase** focused on core business logic:

Covered areas:
- Authorization and ownership validation
- Rating uniqueness constraints
- Favorite add/remove idempotency
- Exception handling and edge cases

During this phase:
- Multiple logic flaws were discovered and corrected
- Code paths were simplified where tests exposed ambiguity
- Defensive programming practices were strengthened

---

## Technical Decisions

- **Language:** Java 24
- **Framework:** Built-in `com.sun.net.httpserver.HttpServer` (no external frameworks)
- **Database:** PostgreSQL with UUID-based user IDs
- **Testing:** JUnit 5 for integration testing + Postman for manual verification
- **Error Handling:** Custom exception system mapping to HTTP response codes
- **Architecture:** Controller–Service–Model separation for SRP compliance
- **Version Control:** Git (branch: `Test`)
- **Authorization:** Centralized Bearer token validation via `AuthUtil`
- **Architecture:** Strict Controller–Service–Repository separation
- **Data Integrity:** Ownership and uniqueness enforced at service level
- **Testing Strategy:** Unit tests prioritized over insecure integration tests
- **Error Handling:** Typed exceptions mapped to HTTP status codes
- **API Validation:** State-consistent behavior for ratings and favorites

---

## Challenges and Solutions

| Challenge | Solution |
|------------|-----------|
| Initial database setup and UUID integration | Switched from `SERIAL` to `UUID` and used `uuid_generate_v4()` |
| Static error handling and duplicated code | Introduced typed exceptions and centralized HTTP response handling |
| Repeated HTTP method checks | Implemented `HttpMethodValidator` utility for clean validation |
| Maintaining clean routing | Moved endpoint definitions from `App.java` into a dedicated `RouteRegistrar` |
| Integration testing issues (server not running) | Added server lifecycle management to ensure tests connect to a live instance |
| Unauthorized media modification | Creator checks enforced in service layer |
| Duplicate ratings or favorites | Existence validation before insert/delete |
| Token misuse across endpoints | Centralized authorization validation |
| Hard-to-test SQL logic | Repository abstraction enabled isolation |
| Hidden edge cases | Unit tests exposed and corrected them |
---

## Final State

At the end of development:
- All **mandatory MRP features** are implemented
- Token-based authorization is enforced globally
- Media ownership rules are strictly validated
- Ratings, favorites, and moderation work as intended
- User-specific history endpoints are functional
- Business logic is covered by unit tests
- The system is **stable, secure, and specification-compliant**

---

## Estimated Time Tracking

| Task                                  | Date   | Estimated Hours |
|---------------------------------------|--------|-----------------|
| Initial setup and DB connection       | Sep 17 | 4h              |
| User registration & Postman testing   | Sep 30 | 4h              |
| Refactoring for SRP & LSP             | Sep 30 | 2h              |
| User get and edit profile integration | Oct 4  | 4h              |
| Exception system and refactor         | Oct 18 | 3h              |
| Integration tests and documentation   | Oct 19 | 3h              |
| Addition of Media CRUD                | Oct 19 | 3h              |
| Repository refactor           | Nov 10 – Nov 13, 2025       | 3h              |
| Auth & token enforcement     | Dec 25, 2025                | 3h              |
| Ratings & favorites system   | Jan 9, 2026                 | 4h              |
| Ownership & access control   | Jan 9, 2026                 | 2h              |
| Unit tests & bug fixes       | Jan 10, 2026                | 3h              |
| **Total**                             |        | **38 hours**     |

