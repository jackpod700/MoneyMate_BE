package com.konkuk.moneymate.auth.templates;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class StockItem {
    private final String reutersCode;
    private final long marketValue;
}
