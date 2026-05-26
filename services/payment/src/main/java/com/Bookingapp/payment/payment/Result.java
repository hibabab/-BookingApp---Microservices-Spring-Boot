package com.Bookingapp.payment.payment;

public record Result(
        Boolean success,
        String payment_id,
        String link,
        String developer_tracking_id

) {}
