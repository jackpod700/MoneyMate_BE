package com.konkuk.moneymate.activities.enums;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
/**
 * 모든 TransactionCategory 필드에 자동 적용 */
@Converter(autoApply = true)
public class TransactionCategoryConverter implements AttributeConverter<TransactionCategory, String> {

    @Override
    public String convertToDatabaseColumn(TransactionCategory attribute) {
        if (attribute == null) return null;
        return attribute.getDisplayName();
    }

    /** DB에는 한글이 저장됩니다
     * DB 한글 → Enum
     */
    @Override
    public TransactionCategory convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return TransactionCategory.fromDisplayName(dbData);
    }
}
