# Nerget Backend

## MBTI Search API

### New Feature: MBTI-based Image Search

The application now supports searching for images based on MBTI personality types.

#### Endpoint

```
GET /api/search/mbti/{code}?limit=24
```

#### Parameters

- `code`: 4-letter MBTI code (e.g., "SFGE", "BCPN")
- `limit`: Maximum number of results to return (default: 24)

#### MBTI Code Format

The MBTI code uses a custom 4-letter format:

- **Axis 1**: S (Sensing) / B (Intuition)
- **Axis 2**: F (Feeling) / C (Thinking)
- **Axis 3**: G (Judging) / P (Perceiving)
- **Axis 4**: E (Extraversion) / N (Introversion)

#### Example Usage

```bash
# Search for SFGE personality type images
curl "http://localhost:8080/api/search/mbti/SFGE?limit=10"

# Search for BCPN personality type images
curl "http://localhost:8080/api/search/mbti/BCPN?limit=5"
```

#### Response Format

```json
[
  {
    "imageId": "uuid-string",
    "imageUrl": "https://s3-url/image.jpg",
    "score": 0
  }
]
```

## Docker Setup

### Prerequisites

- Docker
- Docker Compose

### Running with Docker Compose

```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d --build

# Stop services
docker-compose down
```

### Services

- **API**: Spring Boot application on port 8080
- **PostgreSQL**: Database on port 5432

### Environment Variables

The application uses environment variables for configuration:

- Database connection settings
- Google OAuth credentials
- S3 configuration

## Development

### Local Development

```bash
cd NergetBackend
./gradlew bootRun
```

### Database

The application uses PostgreSQL in Docker environment. For local development, you can:

1. Use the Docker PostgreSQL service
2. Install PostgreSQL locally and update `application.properties`

### CORS

The application is configured to allow requests from:

- `http://localhost:3000`
- `http://localhost:3030`
- `http://192.168.0.6:3000`
- `http://192.168.0.6:3030`
