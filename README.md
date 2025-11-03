# Moneymate Backend (MoneyMate_BE)

> AI-powered Personal Finance Management Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-Latest-red.svg)](https://redis.io/)

## 📋 Overview

**Moneymate-BE** is a comprehensive backend system for personal finance management that integrates AI-powered financial advisory services using OpenAI GPT-4o. The platform provides real-time stock market data, asset management, transaction tracking, and personalized financial recommendations.

## 🚀 Key Features

### 🤖 AI Financial Advisory
- Multiple specialized AI advisors (Asset, Transaction, Consumption, Investment, Portfolio)
- Real-time streaming responses using Server-Sent Events (SSE)
- OpenAI GPT-4o integration with function calling
- Personalized financial advice based on user data

### 💰 Asset Management
- Multi-asset tracking (Bank accounts, Stocks, Cryptocurrency, Real Estate)
- Historical asset value tracking and trend analysis
- Real-time portfolio valuation
- Age-based demographic statistics

### 📈 Stock Market Integration
- Real-time stock price updates via EODHD API
- Korean and US market support
- Historical price data and charts
- Automated daily price updates using Quartz scheduler
- Stock transaction tracking

### 💳 Transaction Management
- Income/Expense categorization
- Bank account integration
- Consumption pattern analysis
- Monthly and yearly financial summaries

### 🏦 Financial Products
- Product recommendations (Savings, Deposits, Loans)
- Integration with Korean Financial Supervisory Service (금융감독원)
- Product comparison and filtering

### 📰 News & Market Intelligence
- Financial news aggregation from multiple sources
- AI-powered news summarization using Google Gemini
- Category-based filtering

### 🎯 Retirement Planning
- Retirement simulation calculator
- Future value projections
- Expense estimation tools

## 🛠 Tech Stack

### Core Framework
- **Spring Boot 3.4.3** with Java 21
- **Spring Data JPA** - Database ORM
- **Spring Security** - JWT-based Authentication
- **Spring AI (1.0.2)** - OpenAI Integration
- **Thymeleaf** - Server-side Templating

### Databases
- **MySQL 8.0** - Primary Database (AWS RDS)
- **Redis** - Session Management & Caching

### External APIs & Libraries
- **OpenAI API** - GPT-4o for financial advisory
- **EODHD API** - Stock market data
- **Google Gemini** - News summarization
- **CoolSMS** - SMS verification
- **EasyCodef** - Korean financial data integration
- **JWT (0.11.5)** - Token-based authentication
- **Quartz** - Job scheduling

## 📂 Project Structure

```
src/main/java/com/konkuk/moneymate/
├── activities/              # Core business domain
│   ├── controller/          # REST API endpoints
│   ├── service/             # Business logic
│   ├── repository/          # Data access
│   ├── entity/              # JPA entities
│   ├── dto/                 # Data transfer objects
│   └── enums/               # Enumerations
├── ai/                      # AI Advisory module
│   ├── controller/          # AI endpoints
│   ├── service/             # AI services
│   └── tools/               # Function calling tools
├── auth/                    # Authentication & Security
│   ├── api/                 # Auth endpoints
│   ├── application/         # JWT & filters
│   ├── service/             # Auth services
│   └── exception/           # Custom exceptions
├── common/                  # Shared utilities
│   ├── templates/           # Test pages & API proxies
│   ├── scheduling/          # Quartz jobs
│   └── ApiResponse.java     # Standard response wrapper
└── MoneymateBeApplication.java  # Main application

src/main/resources/
├── application.properties   # Configuration
├── static/                  # Static assets (CSS, JS)
├── templates/               # Thymeleaf templates
└── sql/                     # Database scripts
```

## 🔧 Setup & Installation

### Prerequisites
- Java 21 JDK
- Gradle 8.x or higher
- MySQL 8.0
- Redis Server
- OpenAI API Key
- EODHD API Key (for stock data)

### Environment Variables

Create environment variables or set in IDE:

```properties
OPENAI_API_KEY=your_openai_api_key
EODHD_API_KEY=your_eodhd_api_key
COOLSMS_API_KEY=your_coolsms_api_key
COOLSMS_API_SECRET=your_coolsms_secret
```

### Database Configuration

Update `src/main/resources/application.properties`:

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://your-host:3306/your-database
spring.datasource.username=your_username
spring.datasource.password=your_password

# Redis Configuration
spring.data.redis.host=your-redis-host
spring.data.redis.port=6379
spring.data.redis.password=your-redis-password
```

### Build & Run

```bash
# Clone the repository
git clone https://github.com/your-org/MoneyMate_BE.git
cd MoneyMate_BE

# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Or run the JAR file
java -jar build/libs/moneymate-BE-0.0.1-SNAPSHOT.jar
```

### Access Points
- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/health

## 📡 API Endpoints

### Authentication
- `POST /api/user/join` - User registration
- `POST /api/user/login` - User login
- `POST /api/user/logout` - User logout
- `POST /api/user/refresh` - Refresh access token

### Assets
- `GET /api/asset/all` - Get all user assets
- `GET /api/asset/total` - Get total asset value
- `POST /api/asset` - Create new asset

### AI Advisor (Streaming)
- `POST /api/advisor/asset/stream` - Asset analysis
- `POST /api/advisor/transaction/stream` - Transaction analysis
- `POST /api/advisor/consumption/stream` - Consumption patterns
- `POST /api/advisor/invest/stream` - Investment recommendations
- `POST /api/advisor/portfolio/stream` - Portfolio analysis

### Stock Market
- `GET /api/stock/holdings` - Get stock holdings
- `GET /api/stock/price/{ticker}` - Get real-time stock price

### Full API documentation available at Swagger UI

## 🔐 Authentication

### JWT-based Authentication
- **Access Token**: Short-lived (configurable expiration)
- **Refresh Token**: Long-lived, stored in Redis
- **Format**: `Authorization: Bearer <token>`

### Security Features
- Custom authentication filter
- Token blacklist for logout
- CORS configuration
- Role-based access control

## 📊 Database Schema

### Core Entities
- **User** - User accounts and authentication
- **BankAccount** - Linked bank accounts
- **Asset** - User assets (various types)
- **Transaction** - Financial transactions
- **Stock** - Stock master data
- **StockPriceHistory** - Historical stock prices
- **AccountStock** - User's stock holdings
- **StockTransaction** - Stock trades
- **ExchangeHistory** - Currency exchange rates
- **News** - Financial news articles

### Statistics Tables
- **StatsConsumption** - Consumption statistics by age
- **StatsAsset** - Asset statistics by age
- **StatsIncome** - Income statistics by age

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests ClassName
```

Test structure mirrors the main source structure in `src/test/java`.

## 📝 Development Notes

### Initial Data Setup
- Run `DataInsert.java` once for initial test data
- Comment out `@Component` after first run to prevent duplicates
- SQL scripts available in `src/main/resources/sql/`

### JPA Configuration
- `spring.jpa.hibernate.ddl-auto=update` - Auto schema updates
- Enable `spring.jpa.show-sql=true` for SQL logging during development

### Scheduled Jobs
- **StockLastdayFetchJob** - Daily stock price updates
- Configured with Quartz (in-memory job store)

## 📈 Recent Updates (Commit History)

### November 2024
- **Nov 03**: MCP backup and configuration updates
- **Oct 26**: BasicTools modifications
- **Oct 15**: Asset Advisor implementation
- **Oct 08**: Daily economic news summarization feature

### September 2024
- **Sep 30**: AI Agent development (Tools, Templates, Services)
- **Sep 24**: Statistics query prototype
- **Sep 15**: Financial products refactoring and CLAUDE.md addition
- **Sep 12**: Financial products scheduler and loan products features
- **Sep 11**: Savings products feature
- **Sep 09**: Time deposit products feature
- **Sep 01**: Scheduling configuration and token management

### August 2024
- **Aug 25**: Stock holdings bug fixes
- **Aug 21**: GitHub Actions CI/CD workflow setup
- **Aug 19**: SQL improvements and JPA configuration
- **Aug 18**: Asset history tracking and consumption statistics
- **Aug 13**: Stock SQL templates
- **Aug 11**: Stock holdings query feature
- **Aug 06**: Database migration to AWS RDS

### July 2024
- **Jul 26**: Stock API proxy improvements
- **Jul 25**: Exchange rate API and test pages
- **Jul 18**: News feature and password reset
- **Jul 17**: SMS authentication
- **Jul 14**: Blacklist logout implementation
- **Jul 07**: Refresh token and logout system
- **Jul 02**: CORS configuration and GitHub templates

### June 2024
- **Jun 08**: AWS cloud deployment
- **Jun 04**: Entity improvements and dummy data
- **Jun 02**: User management features (withdrawal, password reset)

### May 2024
- **May 30**: Retirement simulator
- **May 28**: AWS deployment testing
- **May 24**: Asset and bank account features
- **May 22**: JWT payload UID addition
- **May 20**: Basic login functionality

### April-May 2024
- **Apr 30**: README and EasyCodef library addition
- **Mar 18**: Project initialization

## 🤝 Contributing

### Code Style Guidelines
- Use Lombok annotations for cleaner code
- Follow Spring Framework best practices
- Document complex business logic
- Write unit tests for new features
- Maintain package structure consistency

### Pull Request Process
1. Create a feature branch
2. Implement changes with tests
3. Update documentation if needed
4. Submit PR with clear description
5. Address review comments

## 📄 License

This project is proprietary software developed for Konkuk University's Moneymate application.

## 👥 Team

Developed by the Konkuk University Moneymate Development Team

## 📞 Support

For issues or questions, please create an issue in the GitHub repository.

---

**Last Updated**: November 2024  
**Version**: 0.0.1-SNAPSHOT  
**Spring Boot**: 3.4.3  
**Java**: 21
