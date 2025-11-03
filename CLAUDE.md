# Moneymate Backend

A comprehensive financial management platform backend built with Spring Boot, providing AI-powered financial advisory services, asset management, and real-time stock market data.

## Project Overview

**Moneymate-BE** is a Spring Boot 3.4.3 application that serves as the backend for a personal finance management system. It integrates AI capabilities (OpenAI GPT-4), financial data APIs, and provides RESTful services for managing users' financial assets, transactions, stock portfolios, and retirement planning.

## Tech Stack

### Core Framework
- **Spring Boot 3.4.3** with Java 21
- **Spring Data JPA** - Database ORM
- **Spring Security** - Authentication & Authorization (JWT)
- **Spring AI (1.0.2)** - OpenAI integration for financial advisory
- **Thymeleaf** - Server-side templating
- **Gradle** - Build tool

### Databases
- **MySQL 8.0** - Primary database (AWS RDS)
- **Redis** - Session management and caching

### Key Dependencies
- **JWT (0.11.5)** - Token-based authentication
- **Lombok** - Boilerplate code reduction
- **Spring Quartz** - Job scheduling (stock price updates)
- **SpringDoc OpenAPI** - API documentation (Swagger)
- **CoolSMS** - SMS verification
- **EasyCodef** - Korean financial data integration
- **OkHttp & Gson** - HTTP client and JSON processing
- **Jsoup** - Web scraping for news feeds

## Project Structure

```
src/main/java/com/konkuk/moneymate/
├── activities/              # Core business logic
│   ├── controller/          # REST API controllers
│   ├── dto/                 # Data transfer objects
│   ├── entity/              # JPA entities
│   ├── repository/          # Data access layer
│   ├── service/             # Business logic services
│   ├── enums/               # Enumerations (AccountType, BankCode, etc.)
│   ├── util/                # Utility classes
│   └── validator/           # Input validation
├── ai/                      # AI Advisory Module
│   ├── controller/          # AI advisor endpoints
│   ├── service/             # AI service implementations
│   ├── tools/               # AI function calling tools
│   ├── SpringAiConfig.java  # Spring AI configuration
│   └── MethodConfig.java    # Method registration config
├── auth/                    # Authentication & Authorization
│   ├── api/                 # Auth API controllers
│   ├── application/         # Auth filters and JWT
│   ├── service/             # Auth services
│   ├── templates/           # Test pages & proxies
│   ├── SecurityConfig.java  # Spring Security configuration
│   └── OpenAiConfig.java    # OpenAI configuration
├── common/                  # Shared components
│   ├── ApiResponse.java     # Standard API response wrapper
│   ├── scheduling/          # Quartz job scheduling
│   └── StockPriceApiClient.java
├── config/                  # Application configurations
└── MoneymateBeApplication.java  # Main application entry point

src/main/resources/
├── application.properties   # Application configuration
├── static/                  # Static assets (CSS, JS)
│   ├── css/                 # Legacy stylesheets
│   ├── js/                  # Legacy JavaScript
│   └── lib/                 # Shared utilities
├── templates/               # Thymeleaf templates
│   ├── ai/                  # AI advisor test pages
│   └── test-stockpage.html  # Stock test page
└── sql/                     # Database initialization scripts
```

## Key Features

### 1. AI Financial Advisory
- **Multiple AI Advisors**: Asset, Transaction, Consumption, Investment, and Portfolio Analysis
- **OpenAI Integration**: GPT-4o powered financial advice with function calling
- **Streaming Responses**: Real-time AI response streaming using Server-Sent Events (SSE)
- **Specialized Tools**: Custom tools for each advisor domain (AssetTools, TransactionTools, etc.)

### 2. Asset Management
- Track multiple asset types: bank accounts, stocks, crypto, real estate
- Historical asset value tracking
- Real-time portfolio valuation
- Asset statistics by age demographics

### 3. Stock Market Integration
- Real-time stock price updates (via EODHD API)
- Historical price data
- Stock transaction tracking
- Market index monitoring
- Scheduled price updates using Quartz

### 4. Transaction Management
- Income/expense categorization
- Bank account integration
- Consumption statistics
- Monthly/yearly financial summaries

### 5. Financial Products
- Product recommendations (savings, deposits, loans, etc.)
- Integration with Korean financial services (via Finlife API)
- Product comparison and filtering

### 6. News & Market Intelligence
- Financial news aggregation
- AI-powered news summarization
- Category-based news filtering

### 7. Retirement Simulation
- Retirement planning calculator
- Future value projections
- Expense estimation

## Authentication & Security

### JWT-based Authentication
- **Access Token**: Short-lived token for API access
- **Refresh Token**: Long-lived token stored in Redis
- **Bearer Token**: Authorization header format

### Security Configuration
- Spring Security with custom filters
- `AuthenticationFilter`: JWT validation on protected endpoints
- `AuthEntryPoint`: Custom 401 unauthorized handler
- CORS configuration for frontend integration

### Permitted Endpoints (No Auth Required)
- `/test/**` - Test pages
- `/health/**` - Health checks
- `/api/user/login`, `/api/user/join` - Authentication endpoints
- `/css/**`, `/js/**`, `/images/**` - Static resources
- `/swagger-ui/**`, `/v3/api-docs/**` - API documentation

## Database Schema

### Core Entities
- **User**: User accounts with authentication
- **BankAccount**: Linked bank accounts
- **Asset**: User assets (stocks, cash, etc.)
- **Transaction**: Financial transactions
- **Stock**: Stock master data
- **StockPriceHistory**: Historical stock prices
- **AccountStock**: User's stock holdings
- **StockTransaction**: Stock buy/sell transactions
- **ExchangeHistory**: Currency exchange rates
- **News**: Financial news articles

### Statistics Tables
- **StatsConsumption**: Consumption statistics by age
- **StatsAsset**: Asset statistics by age
- **StatsIncome**: Income statistics by age

## API Endpoints

### Authentication
- `POST /api/user/join` - User registration
- `POST /api/user/login` - User login
- `POST /api/user/logout` - User logout
- `POST /api/user/refresh` - Refresh access token

### Assets
- `GET /api/asset/all` - Get all user assets
- `GET /api/asset/total` - Get total asset value
- `POST /api/asset` - Create new asset

### Transactions
- `GET /api/transaction` - Get transactions
- `POST /api/transaction` - Create transaction

### Stock
- `GET /api/stock/holdings` - Get stock holdings
- `GET /api/stock/price/{ticker}` - Get stock price

### AI Advisor
- `POST /api/advisor/asset/stream` - Asset advisory (streaming)
- `POST /api/advisor/transaction/stream` - Transaction advisory
- `POST /api/advisor/consumption/stream` - Consumption advisory
- `POST /api/advisor/invest/stream` - Investment advisory
- `POST /api/advisor/portfolio/stream` - Portfolio analysis

### Test Pages
- `GET /test/page/agent/v1` - AI Agent Test v1
- `GET /test/page/agent/v2` - AI Agent Test v2 (Asset analysis)
- `GET /test/page/agent/v3` - AI Agent Test v3 (Portfolio analysis)
- `GET /test/page/stock` - Stock price test page

## Environment Variables

Required environment variables (set in IDE or system):

```properties
# API Keys
OPENAI_API_KEY=your_openai_key
EODHD_API_KEY=your_eodhd_key
COOLSMS_API_KEY=your_coolsms_key
COOLSMS_API_SECRET=your_coolsms_secret
```

## Database Configuration

### MySQL (AWS RDS)
```properties
URL: moneymate-db.clceosi6aceb.ap-northeast-2.rds.amazonaws.com:3306/cloud
Username: admin
Password: moneymate
```

### Redis
```properties
Host: 3.37.72.119
Port: 6379
Password: 1122
```

## Running the Application

### Prerequisites
- Java 21 JDK
- Gradle 8.x
- MySQL 8.0
- Redis server
- Environment variables configured

### Build & Run
```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Or run the JAR
java -jar build/libs/moneymate-BE-0.0.1-SNAPSHOT.jar
```

### Access Points
- **API Base**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/health

## Development Notes

### Data Initialization
- `DataInsert.java` - Initial test data population
- `StatsAssetDataInsert.java` - Asset statistics data
- `StatsConsumptionDataInsert.java` - Consumption statistics
- `StatsIncomeDataInsert.java` - Income statistics
- **Note**: Comment out `@Component` after first run to prevent duplicate data

### JPA Configuration
- `spring.jpa.hibernate.ddl-auto=update` - Auto-update schema
- Enable `spring.jpa.show-sql=true` for SQL logging during development

### Scheduled Jobs
- **StockLastdayFetchJob**: Fetches previous day's stock prices
- Runs on Quartz scheduler (in-memory job store)

### Logging
- Info level logging for `com.konkuk.moneymate.*`
- Custom logging in `$custom.*` package

## Testing

### Test Structure
- Test package structure mirrors `main/java` structure
- Use Lombok in tests for cleaner code
- Spring Security Test for auth testing
- JUnit Platform Launcher

### Running Tests
```bash
./gradlew test
```

## API Documentation

Access Swagger UI at: http://localhost:8080/swagger-ui.html

The API uses standard REST conventions:
- `200 OK` - Successful response
- `201 Created` - Resource created
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Authentication required
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Frontend Integration

### Static Resources
- Legacy CSS/JS files in `src/main/resources/static/`
- Thymeleaf templates for test pages
- CORS configured for frontend development

### API Response Format
```json
{
  "success": true,
  "message": "Success message",
  "data": { ... }
}
```

## Known Limitations

- Spring Batch starter included but not yet implemented
- OAuth2 client configured but social login not fully implemented
- Some API endpoints are in Korean (comments/messages)

## Contributing

### Code Style
- Use Lombok annotations (`@Data`, `@AllArgsConstructor`, etc.)
- Follow Spring best practices
- Document complex business logic
- Write tests for new features

### Package Organization
- Controllers: REST endpoints only, delegate to services
- Services: Business logic implementation
- Repositories: Database access (Spring Data JPA)
- DTOs: Data transfer between layers
- Entities: JPA entities with database mappings

## License

This project is proprietary software for Konkuk University's Moneymate application.

## Contact & Support

For issues or questions, please contact the development team.

---

**Last Updated**: November 2024
**Spring Boot Version**: 3.4.3
**Java Version**: 21

