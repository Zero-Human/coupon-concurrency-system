package com.example.coupon.service;

import com.example.coupon.domain.Coupon;
import com.example.coupon.domain.IssuedCoupon;
import com.example.coupon.domain.repository.CouponRepository;
import com.example.coupon.domain.repository.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DbLockCouponIssueService implements CouponIssueService  {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    @Transactional
    public void issue(Long couponId, Long userId) {
        // SELECT ... FOR UPDATE 실행
        Coupon coupon = couponRepository.findByIdWithPessimisticLock(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        coupon.decrease();

        issuedCouponRepository.save(new IssuedCoupon(couponId, userId));
    }
}
