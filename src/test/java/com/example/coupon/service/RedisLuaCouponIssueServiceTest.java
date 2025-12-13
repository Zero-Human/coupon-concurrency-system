package com.example.coupon.service;

import com.example.coupon.domain.Coupon;
import com.example.coupon.domain.repository.CouponRepository;
import com.example.coupon.domain.repository.IssuedCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLuaCouponIssueServiceTest {

    @Autowired
    private RedisLuaCouponIssueService redisLuaCouponIssueService;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;
    @Autowired
    private  StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        issuedCouponRepository.deleteAll();
        issuedCouponRepository.resetAutoIncrement();
        couponRepository.deleteAll();
        couponRepository.resetAutoIncrement();
        Set<String> keys = redisTemplate.keys("coupon:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }

        // 재고 100개짜리 쿠폰 생성
        Coupon coupon = new Coupon("Lua Script 테스트", 100); // 생성자는 실제 코드에 맞게 수정
        couponRepository.save(coupon);
        // 레디스로 재고 100개 짜리 쿠폰으로 이관
        redisTemplate.opsForValue()
                .set("coupon:" + coupon.getId() + ":stock", String.valueOf(100));
    }

    @Test
    void 동시에_요청해도_재고_이상으로_발급되지_않는다() throws Exception {
        int threadCount = 1000;              // 동시에 들어오는 요청 수
        Long couponId = couponRepository.findAll().get(0).getId();

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;

            executorService.submit(() -> {
                try {
                    redisLuaCouponIssueService.issue(couponId, userId);
                } catch (Exception e) {
                    // 재고 소진 등 예외는 무시
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long issuedCount = issuedCouponRepository.count();
        Coupon coupon = couponRepository.findAll().get(0);

        // 발급된 수량은 최대 재고(100)를 넘지 않아야 함
        assertThat(issuedCount).isEqualTo(100L);
    }
}