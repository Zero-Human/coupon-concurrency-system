package com.example.coupon.service;

import com.example.coupon.domain.Coupon;
import com.example.coupon.domain.IssuedCoupon;
import com.example.coupon.domain.repository.CouponRepository;
import com.example.coupon.domain.repository.IssuedCouponRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BaseCouponIssueService implements CouponIssueService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    @Transactional
    public void issue(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        coupon.decrease(); // 동시성 이슈 발생 지점!

        issuedCouponRepository.save(new IssuedCoupon(couponId, userId));
    }
}
