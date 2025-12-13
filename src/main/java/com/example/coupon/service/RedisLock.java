package com.example.coupon.service;

import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class RedisLock {
    private final RedissonClient redissonClient;

    public RLock lock(Long key){
        RLock lock = redissonClient.getLock("lock:"+key);
        try {
            // 락 획득을 시도한다(10초동안 시도를 할 예정이며 획득할 경우 1초안에 해제할 예정이다)
            Boolean isLocked = lock.tryLock(10,1, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("lock 획득 실패");
            }
            return lock;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void unlock(RLock lock){
        if(lock.isLocked() && lock.isHeldByCurrentThread()){
            lock.unlock();
        }
    }
}
