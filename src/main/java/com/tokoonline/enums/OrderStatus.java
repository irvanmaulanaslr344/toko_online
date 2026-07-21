package com.tokoonline.enums;

/**
 * Enum untuk status pesanan.
 * Flow normal: PENDING -> PROCESSED -> SHIPPED -> DELIVERED
 * Bisa dibatalkan: PENDING -> CANCELLED
 */
public enum OrderStatus {
    PENDING,
    PROCESSED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}