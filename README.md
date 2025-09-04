# Course Search API

A Spring Boot application with Elasticsearch for course search functionality, including autocomplete suggestions and fuzzy search capabilities.

## Features

- **Fuzzy search** on course titles (allows small typos) and exact search on descriptions
- **Range filters** for age (minAge/maxAge) and price (minPrice/maxPrice)
- **Exact filters** for category and type
- **Date filtering** for next session date
- **Sorting** options: upcoming (default), priceAsc, priceDesc
- **Pagination** support
- **Fuzzy autocomplete suggestions** for course titles (handles typos)

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven

## Setup and Running

1. **Start Elasticsearch**:
   ```bash
   docker compose up -d
   ```

2. **Verify Elasticsearch is running**:
   ```bash
   curl http://localhost:9200 | cat
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Stop the application and Elasticsearch**:
   ```bash
   docker compose down
   ```

## API Endpoints

### 1. Search Courses

**Endpoint**: `GET /api/search`

**Query Parameters**:
- `q` (optional): Search keyword for title and description
- `minAge` (optional): Minimum age filter
- `maxAge` (optional): Maximum age filter
- `category` (optional): Exact category filter
- `type` (optional): Exact type filter (COURSE, ONE_TIME, CLUB)
- `minPrice` (optional): Minimum price filter
- `maxPrice` (optional): Maximum price filter
- `startDate` (optional): Start date filter (ISO-8601 format)
- `sort` (optional): Sort option (upcoming, priceAsc, priceDesc) - defaults to "upcoming"
- `page` (optional): Page number - defaults to 0
- `size` (optional): Page size - defaults to 10

**Response**:
```json
{
  "total": 60,
  "courses": [
    {
      "id": "0d822c56-84f4-453b-a409-4812dfd18f2e",
      "title": "Data Structures #0",
      "description": "Engaging course covering fundamentals and hands-on practice.",
      "category": "Music",
      "type": "ONE_TIME",
      "gradeRange": "10th–12th",
      "minAge": 10,
      "maxAge": 15,
      "price": 285.27,
      "nextSessionDate": "2025-12-31T10:25:19Z"
    }
  ]
}
```

### 2. Autocomplete Suggestions

**Endpoint**: `GET /api/search/suggest`

**Query Parameters**:
- `q` (required): Partial title to search for

**Response**:
```json
[
  "Data Structures #0",
  "Soccer Skills #1",
  "World Wars #2"
]
```

## API Examples

### Basic Search (All Courses)
```bash
curl "http://localhost:8080/api/search" | jq
```

### Search with Keyword (Fuzzy Matching on Title)
```bash
# Search for courses with "Data" in the title
curl "http://localhost:8080/api/search?q=Data" | jq

# Search for courses with "Soccer" in the title
curl "http://localhost:8080/api/search?q=Soccer" | jq

# Search for courses with "World" in the title
curl "http://localhost:8080/api/search?q=World" | jq
```

### Fuzzy Search Examples (Handles Typos)
The search functionality now supports fuzzy matching on course titles, allowing users to find courses even with small typos:

```bash
# Typo examples that will still find relevant courses
curl "http://localhost:8080/api/search?q=dat" | jq       # Should match "Data Structures"
curl "http://localhost:8080/api/search?q=socer" | jq     # Should match "Soccer Skills"
curl "http://localhost:8080/api/search?q=worl" | jq      # Should match "World Wars"
```

### Filter by Category and Type
```bash
curl "http://localhost:8080/api/search?category=Art&type=ONE_TIME" | jq
```

### Price Range Filter with Sorting
```bash
curl "http://localhost:8080/api/search?minPrice=20&maxPrice=100&sort=priceAsc" | jq
```

### Age Range Filter
```bash
curl "http://localhost:8080/api/search?minAge=10&maxAge=15" | jq
```

### Date Filter (Courses starting from a specific date)
```bash
curl "http://localhost:8080/api/search?startDate=2025-09-10T00:00:00Z" | jq
```

### Complex Search with Multiple Filters
```bash
curl "http://localhost:8080/api/search?category=Art&type=ONE_TIME&minPrice=20&maxPrice=30&sort=priceAsc" | jq
```

### Search with Keyword + Filters
```bash
# Search for Data courses in Music category
curl "http://localhost:8080/api/search?q=Data&category=Music" | jq

# Search for Soccer courses with price range
curl "http://localhost:8080/api/search?q=Soccer&minPrice=200&maxPrice=250" | jq

# Search for World courses with sorting
curl "http://localhost:8080/api/search?q=World&sort=priceDesc" | jq
```

### Pagination
```bash
curl "http://localhost:8080/api/search?page=1&size=5" | jq
```

### Autocomplete Suggestions (Fuzzy Matching)
```bash
# Exact prefix match
curl "http://localhost:8080/api/search/suggest?q=Data" | jq

# Fuzzy autocomplete examples (handles typos)
curl "http://localhost:8080/api/search/suggest?q=dat" | jq     # Should suggest "Data Structures"
curl "http://localhost:8080/api/search/suggest?q=socer" | jq   # Should suggest "Soccer Skills"
curl "http://localhost:8080/api/search/suggest?q=worl" | jq    # Should suggest "World Wars"
```

## Sample Data

The application loads 60 sample courses with various categories:
- Art
- History
- Math
- Music
- Programming
- Science
- Sports

Course types:
- COURSE
- ONE_TIME
- CLUB

## Technical Details

- **Framework**: Spring Boot 3.5.5
- **Database**: Elasticsearch 9.1.3
- **Elasticsearch Java Client**: 9.0.1
- **Java Version**: 17
- **Build Tool**: Maven

## Testing

Run the test suite:
```bash
./mvnw test
```

Generate and view test coverage report:
```bash
./mvnw clean test jacoco:report
open target/site/jacoco/index.html
```

The test suite includes comprehensive coverage for:
- SearchService (all sort branches, pagination, error handling)
- AutocompleteService (empty/null handling, error paths)
- DataLoader (index creation and bulk operations)
- SearchController (endpoint testing with various parameters)
- CourseDocument (model validation)
- ElasticsearchConfig (configuration testing)

## Notes

- **Fuzzy Search**: Implemented on title field using Elasticsearch fuzzy query with "AUTO" fuzziness. Allows small typos (e.g., "dinors" matches "Dinosaurs 101", "javva" matches "Java for Beginners").
- **Multi-field Search**: Searches both title (with 2x boost) and description fields using multi-match query.
- **Autocomplete**: Uses fuzzy matching with wildcard queries to handle typos in autocomplete suggestions.
- **Data Loading**: Sample data is automatically loaded on application startup via CommandLineRunner.
- **Index Management**: Elasticsearch index is automatically created with proper mappings on first run.
- **Error Handling**: All services properly wrap IOException in RuntimeException for consistent error handling.
- **Test Coverage**: Comprehensive test suite with 100% coverage including error paths and edge cases.
- **API Stability**: All search features including fuzzy search, filters, sorting, pagination, and autocomplete are fully functional and tested.
