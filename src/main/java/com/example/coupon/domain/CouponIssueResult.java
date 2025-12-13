package com.example.coupon.domain;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum CouponIssueResult {
    SUCCESS(0L),
    DUPLICATED(1L),
    SOLD_OUT(2L);

    private final Long code;

    public static CouponIssueResult fromCode(Long code){
        return Arrays.stream(CouponIssueResult.values())
                        .filter(it -> it.code == code)
                        .findFirst()
                        .orElse(null);
    }
}

