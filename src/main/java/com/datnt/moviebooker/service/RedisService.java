package com.datnt.moviebooker.service;

import com.datnt.moviebooker.constant.SeatStatus;
import com.datnt.moviebooker.entity.Seat;
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
    private final SeatService seatService;

    public void saveData(String key, String value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean tryLockSeat(Long seatId, Long showTimeId, String username,
                               long timeout, TimeUnit unit) {

        String key = "seat_lock:" + showTimeId + ":" + seatId;

        Boolean ok = redisTemplate.opsForValue()
                .setIfAbsent(key, username, timeout, unit);

        if (Boolean.TRUE.equals(ok)) {
            seatService.updateSeatStatus(seatId, SeatStatus.UNAVAILABLE);
            seatService.clearShowtimeCache(showTimeId);

            long expiresAt = System.currentTimeMillis()
                    + TimeUnit.MILLISECONDS.convert(timeout, unit);

            webSocketService.sendSeatLocked(seatId, showTimeId, username, expiresAt);
        }
        return Boolean.TRUE.equals(ok);
    }

    public void releaseSeat(Long seatId, Long showTimeId) {
        String key = "seat_lock:" + showTimeId + ":" + seatId;
        Boolean existed = redisTemplate.delete(key);
        if (Boolean.TRUE.equals(existed)) {
            seatService.updateSeatStatus(seatId, SeatStatus.AVAILABLE);
            seatService.clearShowtimeCache(showTimeId);
            webSocketService.sendSeatReleased(seatId, showTimeId);
        }
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

                                Long stId = Long.parseLong(parts[1]);
                                Long sId  = Long.parseLong(parts[2]);

                                // Release the seat if it was not booked
                                Seat seat = seatService.findSeatById(sId);
                                if (seat.getStatus() != SeatStatus.BOOKED) {
                                    seatService.updateSeatStatus(sId, SeatStatus.AVAILABLE);
                                    seatService.clearShowtimeCache(stId);
                                    webSocketService.sendSeatReleased(sId, stId);
                                } else {
                                    System.out.println("Seat " + sId + " was booked. Skipping auto-release.");
                                }
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
