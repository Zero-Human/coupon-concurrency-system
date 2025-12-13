package com.example.coupon.domain.repository;

import com.example.coupon.domain.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE issued_coupon AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
