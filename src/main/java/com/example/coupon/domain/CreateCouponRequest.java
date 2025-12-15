package com.example.coupon.domain;

import lombok.Getter;

@Getter
public class CreateCouponRequest {
    private String name;
    private int quantity;
}
