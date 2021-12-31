package com.qm.study.Distributed;

public class TokenBucket {

    /**
     * 令牌桶大小
     */
    private final long capacity;
    /**
     * 表示填充速度，每毫秒填充多少个
     */
    private final double refillTokensPerOneMillis;
    /**
     * 令牌桶可用令牌
     */
    private double availableTokens;
    /**
     * 上一次填充时间
     */
    private long lastRefillTimestamp;

    /**
     *
     * @param capacity   桶大小
     * @param refillTokens 需要生成的令牌
     * @param refillPeriodMillis 生成令牌的时间
     */
    public TokenBucket(long capacity, long refillTokens, long refillPeriodMillis) {
        this.capacity = capacity;
        this.refillTokensPerOneMillis = (double) refillTokens / (double) refillPeriodMillis;
        this.availableTokens = capacity;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    synchronized public boolean tryConsume(int numberTokens) {
        refill();
        if (availableTokens < numberTokens) {
            return false;
        } else {
            availableTokens -= numberTokens;
            return true;
        }
    }

    /**
     *     填充令牌
     */
    private void refill() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > lastRefillTimestamp) {
            long millisSinceLastRefill = currentTimeMillis - lastRefillTimestamp;
            double refill = millisSinceLastRefill * refillTokensPerOneMillis;
            this.availableTokens = Math.min(capacity, availableTokens + refill);
            this.lastRefillTimestamp = currentTimeMillis;
        }
    }

    public static void main(String[] args) {
        //桶大小为 100，且每秒生成 100 个令牌
        TokenBucket limiter = new TokenBucket(100, 100, 1000);
    }
}