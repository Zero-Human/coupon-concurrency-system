package com.example.coupon.controller;

import com.example.coupon.domain.CreateCouponRequest;
import com.example.coupon.domain.IssueRequest;
import com.example.coupon.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final SynchronizedCouponIssueService synchronizedCouponIssueService;
    private final DbLockCouponIssueService dbLockCouponIssueService;
    private final RedisLockCouponIssueService redisLockCouponIssueService;
    private final RedisLuaCouponIssueService luaCouponIssueService;
    private  final BaseCouponIssueService baseCouponIssueService;

    // 1. 문제 발생 API
    @PostMapping("/{id}/issue/synchronized")
    public String issueBasic(@PathVariable Long id, @RequestBody IssueRequest issueRequest) {
        synchronizedCouponIssueService.issue(id, issueRequest.getUserId());
        return "ok";
    }

    // 2. DB 락 API
    @PostMapping("/{id}/issue/db-lock")
    public String issuePessimistic(@PathVariable Long id, @RequestBody IssueRequest issueRequest) {
        dbLockCouponIssueService.issue(id, issueRequest.getUserId());
        return "ok";
    }

    // 3. Redis 락 API
    @PostMapping("/{id}/issue/redisson")
    public String issueRedisson(@PathVariable Long id, @RequestBody IssueRequest issueRequest) {
        redisLockCouponIssueService.issue2(id, issueRequest.getUserId());
        return "ok";
    }
    // 4. Redis LuaScript 사용 API
    @PostMapping("/{id}/issue/redis-lua")
    public String issueRedisLua(@PathVariable Long id, @RequestBody IssueRequest issueRequest) {
        luaCouponIssueService.issue(id, issueRequest.getUserId());
        return "ok";
    }

    @PostMapping("")
    public String resetCoupon(@RequestBody CreateCouponRequest createCouponRequest) {
        baseCouponIssueService.createCoupon(createCouponRequest);
        return "ok";
    }
    @PostMapping("/redis-lua")
    public String resetCouponByRedisLua(@RequestBody CreateCouponRequest createCouponRequest) {
        baseCouponIssueService.createCouponByRedisLua(createCouponRequest);
        return "ok";
    }
    @PostMapping("/redis/reset")
    public String resetCouponByRedisLua() {
        baseCouponIssueService.createCouponByRedisLua(createCouponRequest);
        return "ok";
    }
}
