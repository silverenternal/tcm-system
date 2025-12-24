package com.tcm.utils;

/**
 * 雪花算法ID生成器
 * 用于生成分布式唯一ID
 */
public class SnowflakeIdGenerator {
    
    // 起始时间戳 (2023-01-01)
    private final long twepoch = 1672531200000L;
    
    // 机器ID所占位数
    private final long workerIdBits = 5L;
    
    // 数据中心ID所占位数
    private final long datacenterIdBits = 5L;
    
    // 支持的最大机器ID
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    
    // 支持的最大数据中心ID
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    
    // 序列号所占位数
    private final long sequenceBits = 12L;
    
    // 机器ID偏移量
    private final long workerIdShift = sequenceBits;
    
    // 数据中心ID偏移量
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    
    // 时间戳偏移量
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    
    // 序列号掩码
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    
    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    
    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }
    
    public synchronized long nextId() {
        long timestamp = timeGen();
        
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        
        // 上次生成ID的时间截
        lastTimestamp = timestamp;
        
        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) 
            | (datacenterId << datacenterIdShift) 
            | (workerId << workerIdShift) 
            | sequence;
    }
    
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
    
    protected long timeGen() {
        return System.currentTimeMillis();
    }
    
    public static void main(String[] args) {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);
        for (int i = 0; i < 10; i++) {
            System.out.println(idGenerator.nextId());
        }
    }
}