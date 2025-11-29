package com.xyz.api.service;

import com.xyz.api.dto.ProgressResponse;
import com.xyz.api.enums.ProgressStateEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class ProductProgressService {
    private final RedisTemplate<String,Object> redisTemplate;

    private final String PREFIX = "batch:";
    private final String TOTAL_KEY = "total";
    private final String PROCESSED_KEY = "processed";
    private final String STATE_KEY = "state";


    public void start(String batchId, long totalItem) {
        String progressKey = PREFIX + batchId;
        redisTemplate.opsForHash().put(progressKey, TOTAL_KEY, totalItem);
        redisTemplate.opsForHash().put(progressKey, PROCESSED_KEY, 0L);
        redisTemplate.opsForHash().put(progressKey, STATE_KEY, ProgressStateEnum.RUNNING);
        redisTemplate.expire(progressKey, 12, TimeUnit.HOURS);

        log.info("Progress started : {}", batchId);
    }

    public void updateByOne(String batchId) {
        String progressKey = PREFIX + batchId;
        redisTemplate.opsForHash().increment(progressKey, PROCESSED_KEY, 1L);

        Object total = redisTemplate.opsForHash().get(progressKey, TOTAL_KEY);
        Object processed = redisTemplate.opsForHash().get(progressKey, PROCESSED_KEY);

        if (total != null && processed != null) {
            long _total = Long.parseLong(total.toString());
            long _processed = Long.parseLong(processed.toString());

            if (_processed >= _total) {
                redisTemplate.opsForHash().put(progressKey, STATE_KEY, ProgressStateEnum.COMPLETED);

                log.info("Progress completed : {}", batchId);
            }
        } else {
            log.warn("Progress incompleted : {}", batchId);
        }
    }

    public ProgressResponse status(String batchId) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(PREFIX + batchId);
        if (entries.isEmpty()) {
            return null;
        }
        long total = entries.get(TOTAL_KEY) != null ? Long.parseLong(entries.get(TOTAL_KEY).toString()) : 0;
        long processed = entries.get(PROCESSED_KEY) != null ? Long.parseLong(entries.get(PROCESSED_KEY).toString()) : 0;
        String msg = total == processed ? "Products processing is completed" : "Products processing is still running";

        return new ProgressResponse(batchId, entries.get(STATE_KEY).toString(), total, processed, msg);
    }

    public void failed(String batchId, String error) {
        String progressKey = PREFIX + batchId;
        redisTemplate.opsForHash().put(progressKey, PROCESSED_KEY, ProgressStateEnum.FAILED);
        redisTemplate.opsForHash().put(progressKey, "error", error);

        log.warn("Progress failed for {}, error: {}", batchId, error);
    }
}
