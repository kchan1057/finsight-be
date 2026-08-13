package com.bizradar.trend;

public record CollectLog(
    Long companyId,
    String source,
    String status,
    int fetchedCount,
    int newCount,
    String message
) {}
