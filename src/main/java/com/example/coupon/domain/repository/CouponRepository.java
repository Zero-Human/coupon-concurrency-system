package com.example.coupon.domain.repository;

import com.example.coupon.domain.Coupon;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // [Level 2] 비관적 락 적용 쿼리
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :id")
    Optional<Coupon> findByIdWithPessimisticLock(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE coupon AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
