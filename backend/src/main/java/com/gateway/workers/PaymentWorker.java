package com.gateway.workers;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import java.time.Duration;
import java.util.Random;

@Component
public class PaymentWorker {
    private static final java.util.Map<String, String> fakeDb = new java.util.HashMap<>();

    private static final boolean TEST_MODE =
        Boolean.parseBoolean(System.getenv().getOrDefault("TEST_MODE", "false"));

    private static final boolean TEST_PAYMENT_SUCCESS =
        Boolean.parseBoolean(System.getenv().getOrDefault("TEST_PAYMENT_SUCCESS", "true"));

    private static final int TEST_PROCESSING_DELAY =
        Integer.parseInt(System.getenv().getOrDefault("TEST_PROCESSING_DELAY", "1000"));
        
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void startWorker() {
        new Thread(() -> {
            fakeDb.put("pay_test_1001", "pending");

            while (true) {

                System.out.println("👷 Waiting for job from Redis...");

                String job = redisTemplate.opsForList()
                    .rightPop("payflow_jobs", java.time.Duration.ofSeconds(0));

                if (job == null) {
                    continue;
                }

                System.out.println("📦 Job received: " + job);

                // expected format: process_payment:pay_123:upi
                String[] parts = job.split(":");
                String paymentId = parts[1];
                String method = parts[2];

                System.out.println("💳 Processing payment: " + paymentId);
                try {
                    if (TEST_MODE) {
                        Thread.sleep(TEST_PROCESSING_DELAY);
                    } else {
                        Thread.sleep(5000);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("⚠ Worker interrupted");
                }

                boolean success;

                if (TEST_MODE) {
                    success = TEST_PAYMENT_SUCCESS;
                } else {
                    Random random = new Random();
                    int chance = random.nextInt(100);

                    if ("upi".equals(method)) {
                        success = chance < 90;
                    } else {
                        success = chance < 95;
                    }
                }
                
                
                if (!fakeDb.containsKey(paymentId)) {
                    System.out.println("❌ Payment not found: " + paymentId);
                    continue;
                }

                if (success) {
                    fakeDb.put(paymentId, "success");
                    System.out.println("✅ PAYMENT SUCCESS for " + paymentId);
                } else {
                    fakeDb.put(paymentId, "failed");
                    System.out.println("❌ PAYMENT FAILED for " + paymentId);
                }

                System.out.println("🗂 Current DB status: " + fakeDb.get(paymentId));


                String eventType;

                if (success) {
                    eventType = "payment.success";
                } else {
                    eventType = "payment.failed";
                }

                String webhookJob = "webhook:" + eventType + ":" + paymentId;

                // push webhook job to Redis
                redisTemplate.opsForList().leftPush("webhook_jobs", webhookJob);

                System.out.println("📨 Webhook job queued: " + webhookJob);

            }
        }).start();
    }
}
