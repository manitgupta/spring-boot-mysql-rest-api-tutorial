# Spring Boot, MySQL, JPA, Hibernate Rest API Tutorial

Build Restful CRUD API for a simple Note-Taking application using Spring Boot, Google Cloud Spanner, JPA and Hibernate.

## Requirements

1. Java - 21

2. Maven - 3.x.x

3. Google Cloud Spanner emulator or instance.

## Steps to Setup

**1. Clone the application**

```bash
git clone https://github.com/callicoder/spring-boot-mysql-rest-api-tutorial.git
```

**2. Create Google Cloud Spanner database**
```bash
gcloud spanner instances create my-instance \
    --config=regional-us-central1 \
    --description="My Instance" \
    --nodes=1
gcloud spanner databases create my-db \
    --instance=my-instance
```

**3. Configure application.properties**

+ open `src/main/resources/application.properties`

+ adjust `spring.datasource.url` to match your instance and database properties.

**4. Build and run the app using maven**

```bash
mvn package
java -jar target/easy-notes-1.0.0.jar
```

Alternatively, you can run the app without packaging it using -

```bash
mvn spring-boot:run
```

The app will start running at <http://localhost:8080>.

## Explore Rest APIs

The app defines following CRUD APIs.

    GET /api/notes

    POST /api/notes

    GET /api/notes/{noteId}

    PUT /api/notes/{noteId}

    DELETE /api/notes/{noteId}

You can test them using postman or any other rest client.
