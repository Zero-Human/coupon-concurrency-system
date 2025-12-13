package com.example.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisLockCouponIssueService implements CouponIssueService  {

    private final RedissonClient redissonClient;
    private final BaseCouponIssueService baseCouponIssueService; // 트랜잭션 분리를 위해 주입

    private final RedisLock redisLock;

    public void issue(Long couponId, Long userId) {
        RLock lock = redissonClient.getLock("coupon_lock:" + couponId);

        try {
            // 락 획득 시도 (최대 10초 대기, 1초 점유)
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if (!available) {
                log.info("락 획득 실패");
                return;
            }

            // 락 획득 성공 시 -> 실제 트랜잭션 실행
            // 주의: 트랜잭션 커밋이 완료된 후 락을 풀어야 하므로 별도 서비스/메서드로 분리 추천
            baseCouponIssueService.issue(couponId, userId);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    public void issue2(Long couponId, Long userId) {

        RLock lock = redisLock.lock(couponId);
        try {
            // 락 획득 성공 시 -> 실제 트랜잭션 실행
            // 주의: 트랜잭션 커밋이 완료된 후 락을 풀어야 하므로 별도 서비스/메서드로 분리 추천
            baseCouponIssueService.issue(couponId, userId);
        } finally {
            redisLock.unlock(lock);
        }
    }
}
