# Federal Regulations Analytics Platform

## Summary
This project implements a full-stack web platform that ingests, stores, analyzes, and visualizes U.S. federal regulations sourced from the eCFR.gov API. The platform provides digestible metrics to help identify regulatory trends, detect potential areas for deregulation, and ensure regulatory transparency. The solution includes a backend built with Spring Boot, a frontend developed using React, and MongoDB as the data store. The system supports both manual and scheduled synchronization of eCFR data.

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.2.x
- Spring Data MongoDB
- Spring Web
- Spring Retry
- Spring Scheduler
- Lombok
- SLF4J + Logback

### Frontend
- React 18
- Vite
- TypeScript
- Material-UI
- React Query
- Chart.js
- Axios

### Database
- MongoDB 6.0+
- MongoDB Atlas (for production)

### DevOps
- Docker
- GitHub Actions
- AWS App Runner
- Vercel/S3 + CloudFront

## Dependencies

### Backend Dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.retry</groupId>
        <artifactId>spring-retry</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### Frontend Dependencies
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "@mui/material": "^5.15.0",
    "@tanstack/react-query": "^5.0.0",
    "chart.js": "^4.4.0",
    "axios": "^1.6.0"
  }
}
```

## Central Idea
The Code of Federal Regulations (CFR) is complex and voluminous, with over 200,000 pages across 150+ agencies. This system normalizes and stores CFR data in a hierarchical format (Title > Chapter > Part > Section) and links it to federal agencies. Each regulation section is enriched with calculated metrics and exposed through a public-facing UI for data-driven insights.

## System Architecture

### Backend (Spring Boot)
- Built with Spring Boot 3 and Spring Data MongoDB
- Provides RESTful APIs to query regulations and deliver analysis metrics
- Includes a scheduled cron job to sync data daily
- Exposes an endpoint to manually trigger data synchronization
- Implements batch processing for efficient data ingestion
- Features connection pooling and optimized MongoDB configuration

### Frontend (React)
- Built with React + Vite
- Agency and Title filters, table/chart visualizations, and section detail viewer
- Responsive design for desktop and mobile viewing
- Interactive data visualization components

### Database (MongoDB)
- Stores hierarchical data normalized by agency and section ID
- Indexed for performance; includes metadata, change tracking, and word count
- Optimized connection pooling and write concerns
- Implements bulk operations for efficient data processing

## Data Sync Workflow

### Cron-Based Sync
A Spring scheduled task fetches and processes updated eCFR data daily at 1:00 AM server time.

### Manual Sync Endpoint
A POST /api/sync endpoint allows manual refresh of eCFR data on demand.

### Batch Processing
- Documents are processed in configurable batch sizes (default: 1000)
- Implements bulk operations for efficient MongoDB writes
- Supports parallel processing of batches
- Includes error handling and retry mechanisms

## Metrics and Analysis
- Word Count: Words per section, title, or agency
- Regulatory Density: Custom metric (avg. words/section per agency)
- Change History: Tracks change types and effective dates
- Checksum Validation: SHA-256 of regulation text to ensure integrity

## Deployment
Initial Deployment Plan:
- MongoDB Atlas for database hosting
- Dockerized Spring Boot backend via AWS App Runner
- React frontend via Vercel or S3 + CloudFront

## Configuration
Key configuration parameters:
```yaml
spring:
  data:
    mongodb:
      connection-pool-size: 100
      connection-timeout: 30000
      socket-timeout: 30000
      max-connection-idle-time: 300000
      max-connection-life-time: 1800000
      write-concern: MAJORITY
      read-preference: SECONDARY_PREFERRED

ecfr:
  api:
    batch-size: 50
    bulk-size: 1000
    max-retries: 3
```

## Appendix: MongoDB Schema

### EcfrDocument Collection
```json
{
  "_id": "uuid",
  "title": "48",
  "chapter": "19",
  "part": "1901",
  "section": "1901.101",
  "agency": "Broadcasting Board of Governors",
  "section_heading": "Purpose.",
  "full_text": "The purpose of this part is...",
  "start_date": "2016-12-16",
  "end_date": null,
  "change_types": ["effective", "initial"],
  "word_count": 87,
  "checksum": "a3c94df1d65b...",
  "structure_index": 60832,
  "metadata": {
    "last_updated": "2024-03-14T00:00:00Z",
    "version": "1.0",
    "source": "eCFR API"
  }
}
```

### Indexes
```javascript
db.ecfr_documents.createIndex({ "title": 1, "part": 1, "section": 1 }, { unique: true })
db.ecfr_documents.createIndex({ "agency": 1 })
db.ecfr_documents.createIndex({ "start_date": 1 })
db.ecfr_documents.createIndex({ "word_count": 1 })
```

## Performance Optimizations
1. Connection Pooling
   - Configurable pool size
   - Connection timeout settings
   - Idle connection management

2. Batch Processing
   - Bulk operations for efficient writes
   - Parallel processing of batches
   - Configurable batch sizes

3. MongoDB Configuration
   - Write concern settings
   - Read preference configuration
   - Index optimization
   - Connection monitoring

## Error Handling
- Retry mechanism for failed operations
- Comprehensive error logging
- Transaction management
- Data validation before ingestion 