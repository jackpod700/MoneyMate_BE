package com.konkuk.moneymate.activities.entity.news;

import com.konkuk.moneymate.activities.dto.news.NewsSummarizeDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(schema = "news_summarize")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewsSummarize {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uid;

    @Column(name="content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name="generated_time", nullable = false)
    private LocalDateTime generatedTime;

    @Column(name="category", length = 50, nullable = false)
    private String category;

    public NewsSummarize(String content, String category) {
        this.content = content;
        this.category = category;
        this.generatedTime = LocalDateTime.now();
    }

    public NewsSummarizeDto toDto() {
        return new NewsSummarizeDto(
                this.category,
                this.content,
                this.generatedTime
        );
    }
}
