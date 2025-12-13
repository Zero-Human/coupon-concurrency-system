package com.example.coupon.service;

import com.example.coupon.domain.Coupon;
import com.example.coupon.domain.repository.CouponRepository;
import com.example.coupon.domain.repository.IssuedCouponRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLockCouponIssueServiceTest {

    @Autowired
    private RedisLockCouponIssueService redisLockCouponIssueService ;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @BeforeEach
    void setUp() {
        issuedCouponRepository.deleteAll();
        issuedCouponRepository.resetAutoIncrement();
        couponRepository.deleteAll();
        couponRepository.resetAutoIncrement();

        // 재고 100개짜리 쿠폰 생성
        Coupon coupon = new Coupon("Redis Lock 테스트", 100); // 생성자는 실제 코드에 맞게 수정
        couponRepository.save(coupon);
    }

    @Test
    void 동시에_요청해도_재고_이상으로_발급되지_않는다1() throws Exception {
        int threadCount = 1000;              // 동시에 들어오는 요청 수
        Long couponId = couponRepository.findAll().get(0).getId();

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;

            executorService.submit(() -> {
                try {
                    redisLockCouponIssueService.issue(couponId, userId);
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
        // 쿠폰 엔티티의 남은 수량/발급 수량 필드가 있다면 함께 검증
        assertThat(coupon.getQuantity()).isZero();
    }

    @Test
    void 동시에_요청해도_재고_이상으로_발급되지_않는다2() throws Exception {
        int threadCount = 1000;              // 동시에 들어오는 요청 수
        Long couponId = couponRepository.findAll().get(0).getId();

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;

            executorService.submit(() -> {
                try {
                    redisLockCouponIssueService.issue2(couponId, userId);
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
        // 쿠폰 엔티티의 남은 수량/발급 수량 필드가 있다면 함께 검증
        assertThat(coupon.getQuantity()).isZero();
    }
}