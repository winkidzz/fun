# Medical Diagnosis Recommender

A Spring Boot application that provides medical diagnosis recommendations using Elasticsearch and MongoDB.

## Features

- Medical diagnosis rule management
- Elasticsearch-based search functionality
- MongoDB for data persistence
- RESTful API endpoints
- Comprehensive test coverage

## Prerequisites

- Java 17 or later
- Maven 3.6 or later
- Docker
- VirtualBox (for development environment)
- Ubuntu 24.04 VM (for development environment)

## Development Environment Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/winkidzz/fun.git
   cd fun
   ```

2. Set up VirtualBox VM:
   - Create Ubuntu 24.04 VM named "ubuntu24"
   - Configure port forwarding:
     - Elasticsearch: 9200
     - MongoDB: 27017
     - Docker: 2375
     - SSH: 3322

3. Start required services in VM:
   ```bash
   # Start Elasticsearch
   docker run -d --name elasticsearch -p 9200:9200 -e 'discovery.type=single-node' -e 'xpack.security.enabled=false' docker.elastic.co/elasticsearch/elasticsearch:8.12.2

   # Start MongoDB
   docker run -d --name mongodb -p 27017:27017 mongo:7.0.5
   ```

4. Build the project:
   ```bash
   mvn clean install
   ```

5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── medical/
│   │           ├── config/
│   │           ├── controller/
│   │           ├── model/
│   │           ├── repository/
│   │           └── service/
│   └── resources/
│       ├── application.properties
│       └── default-rules.json
└── test/
    └── java/
        └── com/
            └── medical/
                └── bdd/
```

## Configuration

The application can be configured through `application.properties`:

```properties
# Server
server.port=8080

# Elasticsearch
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.connection-timeout=5s
spring.elasticsearch.socket-timeout=3s

# MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=diagnosis_db

# Logging
logging.level.org.springframework.data.elasticsearch=DEBUG
logging.level.com.medical=DEBUG
```

## Testing

Run tests:
```bash
mvn test
```

## API Endpoints

- `GET /api/diagnosis-rules` - List all diagnosis rules
- `POST /api/diagnosis-rules` - Create a new diagnosis rule
- `GET /api/diagnosis-rules/{id}` - Get a specific diagnosis rule
- `PUT /api/diagnosis-rules/{id}` - Update a diagnosis rule
- `DELETE /api/diagnosis-rules/{id}` - Delete a diagnosis rule
- `POST /api/diagnosis-rules/recommend` - Get diagnosis recommendations

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 