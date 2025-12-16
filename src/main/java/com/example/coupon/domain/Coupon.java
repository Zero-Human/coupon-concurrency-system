package com.example.coupon.domain;

import com.example.coupon.config.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int quantity;

    private int maxQuantity;

    public Coupon(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        this.maxQuantity = quantity;
    }

    // 핵심 비즈니스 로직: 재고 차감
    public void decrease() {
        if (this.quantity <= 0) {
            throw new OutOfStockException();
        }
        this.quantity--;
    }
}
