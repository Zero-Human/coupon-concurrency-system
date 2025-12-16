package com.example.coupon.service;

import com.example.coupon.config.OutOfStockException;
import com.example.coupon.domain.CouponIssueResult;
import com.example.coupon.domain.IssuedCoupon;
import com.example.coupon.domain.repository.IssuedCouponRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class RedisLuaCouponIssueService implements CouponIssueService{

    private final StringRedisTemplate redisTemplate;
    private final IssuedCouponRepository issuedCouponRepository;

    private final DefaultRedisScript<Long> couponIssueScript;

    @Override
    @Transactional
    public void issue(Long couponId, Long userId) {
        String userKey = "coupon:" + couponId + ":user:" + userId;
        String stockKey = "coupon:" + couponId + ":stock";

        Long result = redisTemplate.execute(
                couponIssueScript,
                List.of(userKey, stockKey)
        );
        switch (CouponIssueResult.fromCode(result)) {
            case SUCCESS -> issuedCouponRepository.save(new IssuedCoupon(couponId, userId));
            case DUPLICATED -> {}
            case SOLD_OUT -> throw new OutOfStockException();
            default -> throw new IllegalStateException("Redis Lua 실행 실패");
        }

    }

}
