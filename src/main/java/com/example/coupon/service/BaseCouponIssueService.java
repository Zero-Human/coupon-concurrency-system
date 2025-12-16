package com.example.coupon.service;

import com.example.coupon.domain.Coupon;
import com.example.coupon.domain.CreateCouponRequest;
import com.example.coupon.domain.IssuedCoupon;
import com.example.coupon.domain.repository.CouponRepository;
import com.example.coupon.domain.repository.IssuedCouponRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class BaseCouponIssueService implements CouponIssueService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final StringRedisTemplate redisTemplate;
    @Transactional
    public void issue(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        coupon.decrease(); // 동시성 이슈 발생 지점!

        issuedCouponRepository.save(new IssuedCoupon(couponId, userId));
    }
    @Transactional
    public void createCoupon(CreateCouponRequest createCouponRequest) {
        Coupon coupon = new Coupon(createCouponRequest.getName(), createCouponRequest.getQuantity());
        couponRepository.save(coupon);
    }
    @Transactional
    public void createCouponByRedisLua(CreateCouponRequest createCouponRequest) {
        Coupon coupon = new Coupon(createCouponRequest.getName(), createCouponRequest.getQuantity());
        couponRepository.save(coupon);
        redisTemplate.opsForValue()
                .set("coupon:" + coupon.getId() + ":stock",String.valueOf(coupon.getQuantity()));
    }
    public void redisReset(){
        Set<String> keys = redisTemplate.keys("coupon:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
