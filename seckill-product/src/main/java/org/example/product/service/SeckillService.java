package org.example.product.service;

import com.alibaba.fastjson.JSON;
import org.example.common.dto.SeckillMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SeckillService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /*执行秒杀逻辑*/
    public String doSeckill(Long userId, Long itemId) {
        // 幂等性检查：同一个用户半小时内只能对同一个商品下一单
        String limitKey = "seckill:limit:" + itemId + ":" + userId;
        Boolean isFirst = redisTemplate.opsForValue().setIfAbsent(limitKey, "1", Duration.ofMinutes(30));

        if (Boolean.FALSE.equals(isFirst)) {
            return "请勿重复下单";
        }

        // Redis 预减库存
        String stockKey = "seckill:stock:" + itemId;
        Long stock = redisTemplate.opsForValue().decrement(stockKey);

        if (stock < 0) {
            // 库存不足，要把刚才多减的加回去，保持数据一致
            redisTemplate.opsForValue().increment(stockKey);
            return "商品已售罄";
        }

        // 扣减成功，将订单请求发送到 Kafka
        SeckillMessage message = new SeckillMessage(userId, itemId);
        // 将对象转为 JSON 字符串发送
        kafkaTemplate.send("seckill_topic", JSON.toJSONString(message));

        return "抢购成功，正在排队中...";
    }
}