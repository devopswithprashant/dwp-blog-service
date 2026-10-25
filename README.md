# Spring Boot Blog REST API

> Follow the separate documentation for complete CI/CD pipeline & SDLC workflow. [visit here](https://gitlab.com/my-first-group5031428/blog-backend-restapi/-/wikis/Home)

## Overview

This project is a Spring Boot-based RESTful API designed to perform CRUD (Create, Read, Update, Delete) operations on blog posts. It serves as the backend for a blog application, providing endpoints that can be consumed by a frontend (such as a React app).


## Architecture
1. Framework: Spring Boot
1. Language: Java
1. Build Tool: Maven
1. Database: (Configured in application.properties, typically PostgreSQL/MySQL)
1. Pattern: Layered Architecture (Controller → Service → Repository → Entity)



## Project Structure


| Layer	      |    File       |	    Description             |
|-------------|---------------|-----------------------------|
| Controller Layer  |	BlogController.java |	Handles HTTP requests (GET, POST, PUT, DELETE) and exposes API endpoints for blog management. |
| Service Layer	 | BlogService.java	 | Contains business logic; interacts with the repository to perform CRUD operations. |
| Repository Layer | 	BlogRepository.java |	Extends Spring Data JPA’s JpaRepository to interact with the database. |
| Entity Layer |	Blog.java  |	Represents the blog post table with fields like title, content, author, publish date, and last modified date. |
| Configuration	| CorsConfig.java |	Enables CORS (Cross-Origin Resource Sharing) to allow frontend apps (like React) to call the API. |
| Application Config	| application.properties |	Contains database credentials, server port, and other environment configurations. |




## API Endpoints

| Method	  |  Endpoint	  | Authentication | Description |
|-------------|---------------|----------------|-------------|
| GET	      | /api/blogs	  | Public | Fetch paginated blog posts |
| GET	      | /api/blogs/{id}/metadata | Public | Fetch blog metadata |
| GET	      | /api/blogs/{id}/content | Public | Fetch blog content |
| POST        |	/api/blogs  | Bearer JWT required | Create a draft owned by the authenticated user |
| PUT	      | /api/blogs/{id} | Owner's bearer JWT required | Update an owned blog |
| DELETE      |	/api/blogs/{id} | Owner's bearer JWT required | Delete an owned blog |
| POST        | /api/blogs/{id}/publish | Owner's bearer JWT required | Publish an owned blog |

Note: The CORS configuration allows requests from frontend applications hosted on a different origin.

## Authentication and blog ownership

The service validates Auth Service access tokens directly. Configure `JWT_SECRET`
with the same HS384 signing secret used by Auth Service (at least 48 UTF-8 bytes).
The expected issuer and audience default to `devopswithprashant-auth` and
`devopswithprashant-platform`; override them with `JWT_ISSUER` and `JWT_AUDIENCE`
only when the platform contract differs. The token must have a valid expiry and
a UUID `sub`.

Blog creation accepts only blog content; author identity is taken from the
verified token's `sub`, never from request data. Blog metadata includes
`authorUuid`, which is the stable Auth identity, and an optional `authorProfile`
projection loaded from the Profile Service's public
`GET /api/v1/profiles/{profileId}` endpoint. The projection contains only
`userId`, `displayName`, `bio`, `avatarUrl`, and `createdAt`. Configure
`PROFILE_SERVICE_BASE_URL` for the Profile Service origin. This setting must be
provided for each environment; no service hostname is hardcoded. Lookups use
bounded timeouts and a bounded cache (maximum 1,000 entries, 10-minute TTL by
default). Profile `404` and temporary
service failures omit `authorProfile` without preventing blog metadata from
loading. Update, delete, and publish requests are allowed only when the token
subject matches `authorUuid`; other users receive `403 Forbidden`, and
missing/invalid tokens receive `401 Unauthorized`. Existing posts without an
`authorUuid` remain readable but cannot be managed until ownership is assigned
through an explicit trusted backfill.

When Profile Service is unavailable or no profile exists, `authorProfile` is
`null`; `authorUuid` remains available as the stable author reference. Example:

```json
{
  "authorUuid": "8c0c0b42-ff2a-4d68-b90f-3b845ca8106a",
  "authorProfile": {
    "userId": "8c0c0b42-ff2a-4d68-b90f-3b845ca8106a",
    "displayName": "Ada",
    "bio": "Engineer",
    "avatarUrl": "https://images.example.test/ada.png",
    "createdAt": "2026-10-18T12:00:00Z"
  }
}
```

Example authenticated creation body:

```json
{
  "title": "My First Blog Post",
  "markdown": "This is an introductory post."
}
```

Send the Auth Service access token as `Authorization: Bearer <token>`.


## Data Model

Entity: Blog
```json
{
  "id": Long,
  "title": String,
  "content": String,
  "authorUuid": "UUID",
  "publishDate": LocalDateTime,
  "lastModifiedDate": LocalDateTime
}
```



## Key Features

1. RESTful architecture following best practices
1. JPA and Hibernate integration for database operations
1. CORS enabled for cross-origin frontend communication
1. Modular and maintainable code with clear separation of concerns



## Usage
1.  Clone the project
1.	Make sure the mentioned database running or you can provide your db info in pom.xml maven `dev` profile
1.	Run using:
```bash
mvn clean install -P dev -DskipTests
```

## Access API at:

http://localhost:8080/api/blogs



## Example API Call

POST /api/blogs
```json
{
  "title": "My First Blog Post",
  "markdown": "This is an introductory post about Spring Boot."
}
```



## Future Enhancements 
1. Add pagination and sorting for blog listings
1. Add search and filter functionality
1. Integrate Swagger UI for API documentation

