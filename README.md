# Spring Boot Blog REST API

Follow the page for complete CI/CD pipeline & SDLC workflow. [visit here](https://gitlab.com/my-first-group5031428/blog-backend-restapi/-/wikis/Home)

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

| Method	  |  Endpoint	  |         Description        |
|-------------|---------------|-----------------------------|
| GET	      | /api/blogs	  | Fetch all blog posts        |
| GET	      | /api/blogs/{id}  |	Fetch a specific blog post by ID    |
| POST        |	/api/blogs  |	Create a new blog post     |
| PUT	      | /api/blogs/{id} |	Update an existing blog post        |
| DELETE      |	/api/blogs/{id} |	Delete a blog post by ID       |

Note: The CORS configuration allows requests from frontend applications hosted on a different origin.



## Data Model

Entity: Blog
```json
{
  "id": Long,
  "title": String,
  "content": String,
  "author": String,
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

http://localhost:9090/api/blogs



## Example API Call

POST /api/blogs
```json
{
  "title": "My First Blog Post",
  "content": "This is an introductory post about Spring Boot.",
  "author": "Prashant",
  "publishDate": "2025-10-06T20:00:00",
  "lastModifiedDate": "2025-10-06T20:00:00"
}
```



## Future Enhancements 
1. Add pagination and sorting for blog listings
1. Implement user authentication (JWT/Spring Security)
1. Add search and filter functionality
1. Integrate Swagger UI for API documentation






