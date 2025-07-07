package com.datnt.moviebooker.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;

import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final RedisMessageListenerContainer redisContainer;
    private final WebSocketService webSocketService;

    public void saveData(String key, String value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean tryLockSeat(Long seatId, Long showTimeId, Long userId, long timeout, TimeUnit timeUnit) {
        String key = "seat_lock:" + showTimeId + ":" + seatId;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, userId.toString(), timeout, timeUnit);
        return Boolean.TRUE.equals(success); // if success is null, return false
    }

    public void releaseSeat(Long seatId, Long showTimeId) {
        String key = "seat_lock:" + showTimeId + ":" + seatId;
        // delete key to release the seat
        redisTemplate.delete(key);
    }

    // Save data for any type
    public <T> void saveDataGeneric(String key, T value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value.toString(), timeout, timeUnit);
    }

    // Get data for any type
    public <T> T getDataGeneric(String key, Class<T> clazz) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (clazz == String.class) {
            return clazz.cast(value);
        } else if (clazz == Integer.class) {
            return clazz.cast(Integer.parseInt(value));
        } else if (clazz == Long.class) {
            return clazz.cast(Long.parseLong(value));
        } else if (clazz == Boolean.class) {
            return clazz.cast(Boolean.parseBoolean(value));
        }
        throw new IllegalArgumentException("Unsupported data type: " + clazz.getName());
    }

    // Delete a key
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    // Check if a key exists
    public boolean keyExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Find keys by pattern
    public Set<String> findKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    // Extend the expiration time of a key
    public boolean extendKeyExpiration(String key, long timeout, TimeUnit timeUnit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, timeUnit));
    }

    // Get the remaining expiration time of a key
    public Long getKeyExpiration(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    @PostConstruct
    private void init() {
        // Listen to expired keys to release seats automatically
        redisContainer.addMessageListener(
                new MessageListenerAdapter(new MessageListener() {
                    // This method will be called when a key is expired
                    @Override
                    public void onMessage(Message message, byte[] pattern) {
                        try {
                            // Get the expired key
                            String expiredKey = new String(message.getBody());

                            // Check if the key is a seat lock key
                            if (expiredKey.startsWith("seat_lock:")) {
                                String[] parts = expiredKey.split(":");
                                if (parts.length < 3) {
                                    System.err.println("Invalid expired key format: " + expiredKey);
                                    return;
                                }

                                Long showTimeId = Long.parseLong(parts[1]);
                                Long seatId = Long.parseLong(parts[2]);

                                // send message to WebSocket
                                webSocketService.sendSeatReleased(seatId, showTimeId);
                            }
                        } catch (Exception e) {
                            System.err.println("Error processing expired key: " + e.getMessage());
                        }
                    }
                }),
                new PatternTopic("__keyevent@0__:expired")
        );
    }
}
