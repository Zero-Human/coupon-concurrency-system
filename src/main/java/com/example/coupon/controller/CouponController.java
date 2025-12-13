package com.example.coupon.controller;

import com.example.coupon.service.SynchronizedCouponIssueService;
import com.example.coupon.service.DbLockCouponIssueService;
import com.example.coupon.service.RedisLockCouponIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final SynchronizedCouponIssueService synchronizedService;
    private final DbLockCouponIssueService bbLockService;
    private final RedisLockCouponIssueService redisLockService;

    // 1. 문제 발생 API
    @PostMapping("/{id}/issue/synchronized")
    public String issueBasic(@PathVariable Long id, @RequestBody Long userId) {
        synchronizedService.issue(id, userId);
        return "ok";
    }

    // 2. DB 락 API
    @PostMapping("/{id}/issue/db-lock")
    public String issuePessimistic(@PathVariable Long id, @RequestBody Long userId) {
        bbLockService.issue(id, userId);
        return "ok";
    }

    // 3. Redis 락 API
    @PostMapping("/{id}/issue/redisson")
    public String issueRedisson(@PathVariable Long id, @RequestBody Long userId) {
        redisLockService.issue(id, userId);
        return "ok";
    }
}
