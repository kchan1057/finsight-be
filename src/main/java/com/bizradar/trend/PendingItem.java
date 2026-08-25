package com.bizradar.trend;

public record PendingItem(
    Long id,
    String title,
    String source,
    String companyName
) {}
