package com.Bookingapp.payment.payment;

public record Request(
        String app_token,
        String app_secret,
        String accept_card,
        int amount,
        String success_link,
        String fail_link,
        int session_timeout_secs,
        String developer_tracking_id
) {}
