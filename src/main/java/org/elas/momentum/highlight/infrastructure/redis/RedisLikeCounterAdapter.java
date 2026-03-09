package org.elas.momentum.highlight.infrastructure.redis;

import org.elas.momentum.highlight.domain.port.out.HighlightLikeCounterPort;
import org.elas.momentum.highlight.infrastructure.persistence.HighlightJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
class RedisLikeCounterAdapter implements HighlightLikeCounterPort {

    private static final Logger log = LoggerFactory.getLogger(RedisLikeCounterAdapter.class);
    private static final String KEY_PREFIX = "highlight:likes:";

    private final StringRedisTemplate redisTemplate;
    private final HighlightJpaRepository highlightJpaRepository;

    RedisLikeCounterAdapter(StringRedisTemplate redisTemplate,
                            HighlightJpaRepository highlightJpaRepository) {
        this.redisTemplate = redisTemplate;
        this.highlightJpaRepository = highlightJpaRepository;
    }

    @Override
    public void increment(String highlightId) {
        redisTemplate.opsForValue().increment(key(highlightId));
    }

    @Override
    public void decrement(String highlightId) {
        Long current = redisTemplate.opsForValue().decrement(key(highlightId));
        if (current != null && current < 0) {
            redisTemplate.opsForValue().set(key(highlightId), "0");
        }
    }

    @Override
    public long getCount(String highlightId) {
        String val = redisTemplate.opsForValue().get(key(highlightId));
        if (val == null) return 0L;
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            log.warn("Invalid Redis like count for {}: {}", highlightId, val);
            return 0L;
        }
    }

    @Override
    public void syncToDatabase(String highlightId, long count) {
        highlightJpaRepository.findById(highlightId).ifPresent(entity -> {
            entity.setLikeCount((int) Math.min(count, Integer.MAX_VALUE));
            highlightJpaRepository.save(entity);
            log.debug("Synced like count {} for highlight {}", count, highlightId);
        });
    }

    private String key(String highlightId) {
        return KEY_PREFIX + highlightId;
    }
}
