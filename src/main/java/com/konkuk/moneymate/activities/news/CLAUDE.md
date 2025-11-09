# News Package (`com.konkuk.moneymate.activities.news`)

## Overview

The `news` package manages financial news collection, summarization, and delivery. It scrapes news from various sources, uses AI to summarize content, and provides categorized news feeds to users.

## Package Structure

```
news/
├── controller/
│   └── NewsController.java           # REST API for news
├── dto/
│   ├── NewsDto.java                  # News data transfer object
│   └── NewsSummarizeDto.java         # News summary data transfer
├── entity/
│   └── NewsSummarize.java            # News summary entity
├── enums/
│   ├── NewsCategoryCode.java         # News category codes
│   └── NewsPublisher.java            # News publisher codes
├── repository/
│   └── NewsSummarizeRepository.java  # News data access
└── service/
    ├── NewsCollector.java            # Collect news from sources
    ├── NewsExtractor.java            # Extract news content
    ├── NewsService.java              # News business logic
    └── NewsSummarizer.java           # AI-powered summarization
```

## Core Components

### 1. NewsController

**Purpose**: REST API endpoints for financial news

**Base URL**: `/api/news`

**Endpoints**:

#### `GET /api/news/latest`
**Description**: Get latest news articles  
**Query Params**: `category` (optional), `limit` (default: 10)  
**Response**: List of NewsSummarizeDto

#### `GET /api/news/{newsId}`
**Description**: Get specific news article with summary  
**Path Variable**: `newsId`  
**Response**: NewsSummarizeDto

#### `GET /api/news/by-category`
**Description**: Get news filtered by category  
**Query Params**: `category` (STOCK, ECONOMY, CRYPTOCURRENCY, etc.)  
**Response**: List of NewsSummarizeDto

#### `GET /api/news/trending`
**Description**: Get trending financial news  
**Response**: List of popular news articles

#### `POST /api/news/refresh`
**Description**: Manually trigger news refresh  
**Auth**: Admin only  
**Response**: Success message with count

---

### 2. NewsService

**Purpose**: Business logic for news management

**Key Methods**:

##### `getLatestNews(NewsCategoryCode category, int limit)`
- Retrieve latest news from database
- Filter by category if specified
- Return list of summarized news

##### `getNewsById(Long newsId)`
- Retrieve specific news article
- Include full summary and metadata
- Return NewsSummarizeDto

##### `getNewsByCategory(NewsCategoryCode category)`
- Filter news by category
- Sort by publication date (descending)
- Return categorized news list

##### `refreshNews()`
- Trigger news collection from all sources
- Process and summarize new articles
- Store in database
- Return count of new articles

---

### 3. NewsCollector

**Purpose**: Collect news from various financial news sources

**Supported Sources**:
- Naver Finance
- Daum Finance
- Investing.com
- Bloomberg (via RSS)

**Key Methods**:

##### `collectFromNaver(NewsCategoryCode category)`
- Scrape news from Naver Finance
- Parse HTML using Jsoup
- Extract title, content, link, date
- Return list of raw news data

##### `collectFromDaum(NewsCategoryCode category)`
- Scrape news from Daum Finance
- Similar extraction process
- Return list of raw news data

##### `collectAllNews()`
- Collect from all sources
- Aggregate results
- Remove duplicates
- Return unified news list

**Scheduled Collection**:
```java
@Scheduled(cron = "0 */30 * * * *")  // Every 30 minutes
public void scheduledNewsCollection() {
    collectAllNews();
}
```

---

### 4. NewsExtractor

**Purpose**: Extract and clean news content from HTML

**Key Methods**:

##### `extractTitle(Document doc)`
- Parse HTML document
- Find title element
- Clean and normalize text
- Return title string

##### `extractContent(Document doc)`
- Find main content element
- Remove ads and scripts
- Clean HTML tags
- Return plain text content

##### `extractPublicationDate(Document doc)`
- Parse date from various formats
- Convert to LocalDateTime
- Handle timezone
- Return standardized date

##### `extractImageUrl(Document doc)`
- Find main article image
- Get highest quality version
- Return image URL

---

### 5. NewsSummarizer

**Purpose**: AI-powered news summarization

**Integration**: Uses OpenAI GPT-4 for summarization

**Key Methods**:

##### `summarizeNews(String title, String content)`
- Send article to OpenAI API
- Request concise summary (2-3 sentences)
- Extract key points
- Return summary string

##### `summarizeBatch(List<NewsDto> newsList)`
- Batch summarize multiple articles
- Optimize API calls
- Return list of summaries

**Prompt Template**:
```
다음 금융 뉴스 기사를 2-3문장으로 요약해주세요:

제목: {title}
내용: {content}

요약:
```

---

### 6. Entities

#### NewsSummarize

**Table**: `news_summarize`

**Key Fields**:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String title;              // Article title
private String originalUrl;        // Original article URL
private String imageUrl;           // Article image URL

@Enumerated(EnumType.STRING)
private NewsCategoryCode category; // News category

@Enumerated(EnumType.STRING)
private NewsPublisher publisher;   // News source

@Column(columnDefinition = "TEXT")
private String summary;            // AI-generated summary

@Column(columnDefinition = "TEXT")
private String originalContent;    // Full article content (optional)

private LocalDateTime publishedAt; // Publication date
private LocalDateTime createdAt;   // Collection date
private Integer viewCount;         // View count
```

---

### 7. Enums

#### NewsCategoryCode
```java
public enum NewsCategoryCode {
    STOCK("주식"),
    ECONOMY("경제"),
    CRYPTOCURRENCY("가상화폐"),
    REAL_ESTATE("부동산"),
    BANKING("은행"),
    INSURANCE("보험"),
    GLOBAL("국제");
}
```

#### NewsPublisher
```java
public enum NewsPublisher {
    NAVER("네이버"),
    DAUM("다음"),
    INVESTING("인베스팅닷컴"),
    BLOOMBERG("블룸버그"),
    REUTERS("로이터");
}
```

---

## Business Logic

### News Collection Workflow
1. **Scrape Sources**: Collect from multiple sources
2. **Extract Content**: Parse and clean HTML
3. **Deduplicate**: Remove duplicate articles
4. **Summarize**: Generate AI summaries
5. **Store**: Save to database
6. **Notify**: (Optional) Send notifications

### News Categorization
- **Auto-categorization**: Based on keywords and source
- **Manual Override**: Admin can recategorize
- **Multi-category**: Articles can belong to multiple categories

### Trending Algorithm
1. **View Count**: Recent view activity
2. **Recency**: Publication date weight
3. **Engagement**: (Future) Likes, shares, comments
4. **Score**: Weighted combination

---

## Dependencies

### External
- Spring Data JPA
- Spring Web
- Jsoup (HTML parsing)
- OpenAI API (via Spring AI)
- Spring Scheduling

### Internal
- `common.ApiResponse` - Response wrapper
- `ai.service.PromptTemplateService` - AI prompt generation

---

## Security

- News endpoints are public (no auth required)
- Refresh endpoint requires admin authentication
- API key for OpenAI stored securely

---

## API Response Format

```json
{
  "status": "OK",
  "message": "조회 성공",
  "data": [
    {
      "id": 1,
      "title": "코스피, 외국인 순매수에 상승 마감",
      "summary": "코스피가 외국인의 대규모 순매수에 힘입어 상승 마감했습니다. 기술주와 금융주가 강세를 보였으며, 시장 전문가들은 긍정적 전망을 유지하고 있습니다.",
      "category": "STOCK",
      "publisher": "NAVER",
      "imageUrl": "https://...",
      "originalUrl": "https://...",
      "publishedAt": "2024-11-09T15:30:00",
      "viewCount": 1234
    }
  ]
}
```

---

## Scheduled Tasks

### News Collection
```java
@Scheduled(cron = "0 */30 * * * *")  // Every 30 minutes
public void collectNews() {
    newsCollector.collectAllNews();
}
```

### Cleanup Old News
```java
@Scheduled(cron = "0 0 2 * * *")  // Daily at 2 AM
public void cleanupOldNews() {
    // Delete news older than 30 days
    LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
    newsRepository.deleteByCreatedAtBefore(cutoff);
}
```

---

## Best Practices

1. **Rate Limiting**: Respect news source rate limits
2. **Caching**: Cache news list for performance
3. **Error Handling**: Handle scraping failures gracefully
4. **Content Quality**: Validate extracted content
5. **API Costs**: Batch summarization to reduce OpenAI API costs

---

## Troubleshooting

### Common Issues

**Issue**: News not updating  
**Solution**: Check scheduled task is running, verify source URLs

**Issue**: Summarization failing  
**Solution**: Check OpenAI API key, verify API quota

**Issue**: Duplicate news  
**Solution**: Improve deduplication logic based on title/URL

**Issue**: Slow news loading  
**Solution**: Add caching, optimize database queries

---

## Future Enhancements

- [ ] Add user preferences for news categories
- [ ] Implement full-text search
- [ ] Add news bookmarking feature
- [ ] Support more news sources
- [ ] Add real-time push notifications
- [ ] Implement sentiment analysis
- [ ] Add news translation (multilingual)

---

**Package Owner**: News Team  
**Last Updated**: November 2024  
**Related Documentation**:
- [Parent: activities/README.md](../README.md)
- [AI Package: ../../ai/CLAUDE.md](../../ai/CLAUDE.md)

