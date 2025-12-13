package com.example.coupon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisLuaConfig {

    private static final String COUPON_ISSUE_LUA =
            """
            -- KEYS[1] = userKey,  KEYS[2] = stockKey
                                
            local userKey = KEYS[1]
            local stockKey = KEYS[2]
                                
            -- 1. 중복 발급 체크
            if redis.call("EXISTS", userKey) == 1 then
              return 1
            end
                                
            -- 2. 현재 재고 조회
            local stock = tonumber(redis.call("GET", stockKey) or "0")
            if stock <= 0 then
              return 2
            end
                                
            -- 3. 재고 차감
            redis.call("DECR", stockKey)
                                
            -- 4. 유저 발급 기록 (TTL은 선택)
            redis.call("SET", userKey, "1")
                                
            return 0
            """;


    @Bean
    public DefaultRedisScript<Long> couponIssueScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(COUPON_ISSUE_LUA);
        script.setResultType(Long.class);
        return script;
    }
}
