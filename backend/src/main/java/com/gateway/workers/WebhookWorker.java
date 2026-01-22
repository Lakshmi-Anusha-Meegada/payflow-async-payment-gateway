package com.gateway.workers;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.Random;

@Component
public class WebhookWorker {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void startWorker() {
        new Thread(() -> {
            while (true) {

                System.out.println("📡 Waiting for webhook job...");

                String job = redisTemplate.opsForList()
                        .rightPop("webhook_jobs", java.time.Duration.ofSeconds(0));

                if (job == null) {
                    continue;
                }

                boolean delivered;

                Random random = new Random();
                delivered = random.nextInt(100) < 70; // 70% success

                if (delivered) {
                    System.out.println("✅ Webhook delivered: " + job);
                } else {
                    System.out.println("❌ Webhook failed, retrying later: " + job);

                    // simple retry: push back to queue
                    redisTemplate.opsForList().leftPush("webhook_jobs", job);

                    try {
                        Thread.sleep(3000); // wait before retry
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }


                // simulate delivery
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                System.out.println("✅ Webhook delivered: " + job);
            }
        }).start();
    }
}
