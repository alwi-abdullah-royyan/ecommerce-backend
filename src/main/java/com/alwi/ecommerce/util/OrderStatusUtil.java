package com.alwi.ecommerce.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OrderStatusUtil {

    // Main Order Statuses
    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        ON_TRANSIT,
        DELIVERED,
        CANCELLED,
        RETURNED,
        REJECTED,
        REFUNDED,
        COMPLETED
    }

    // Allowed status transitions based on current status
    public static List<OrderStatus> getAllowedStatusTransitions(OrderStatus currentStatus) {
        switch (currentStatus) {
            case PENDING:
                return Arrays.asList(OrderStatus.CONFIRMED, OrderStatus.CANCELLED);
            case CONFIRMED:
                return Arrays.asList(OrderStatus.PROCESSING, OrderStatus.CANCELLED);
            case PROCESSING:
                return Arrays.asList(OrderStatus.ON_TRANSIT, OrderStatus.CANCELLED);
            case ON_TRANSIT:
                return Arrays.asList(OrderStatus.DELIVERED, OrderStatus.RETURNED);
            case DELIVERED:
                return Arrays.asList(OrderStatus.RETURNED, OrderStatus.COMPLETED);
            case RETURNED:
                return Collections.singletonList(OrderStatus.REFUNDED);
            case CANCELLED:
            case REJECTED:
            case REFUNDED:
            default:
                return Collections.emptyList();
        }
    }
    public static boolean willCauseProductReturn(OrderStatus newStatus) {
        return newStatus == OrderStatus.RETURNED
                || newStatus == OrderStatus.CANCELLED
                || newStatus == OrderStatus.REFUNDED;
    }

}
