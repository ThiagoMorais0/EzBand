package com.baseapplication.core.dto;

public record PushSubscriptionRequest(String endpoint, String p256dh, String auth) {}
