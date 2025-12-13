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
public class SynchronizedCouponIssueService implements CouponIssueService {

    private final BaseCouponIssueService baseCouponIssueService;



    public synchronized void issue(Long couponId, Long userId) {
        baseCouponIssueService.issue(couponId,userId);
    }
}
