package com.nckueat.foodsmap.component.SnowflakeId;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.nckueat.foodsmap.properties.SnowflakeProperties;

@Component
@EnableConfigurationProperties(SnowflakeProperties.class)
public class SnowflakeIdGenerator {
    private final long twepoch = 1743782400000L;

    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long sequenceBits = 12L;

    private final long sequenceMask = ~(-1L << sequenceBits);

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(SnowflakeProperties properties) {
        this.workerId = properties.getWorkerId();
        this.datacenterId = properties.getDatacenterId();
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards. Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        long result = ((timestamp - twepoch) << datacenterIdBits) | datacenterId;
        result = (result << workerIdBits) | workerId;
        result = (result << sequenceBits) | sequence;

        return result;
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
}
