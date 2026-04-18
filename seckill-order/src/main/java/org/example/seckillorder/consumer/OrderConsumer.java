package org.example.seckillorder.consumer;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.common.dto.SeckillMessage;
import org.example.seckillorder.entity.Order;
import org.example.seckillorder.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    @Autowired
    private OrderMapper orderMapper;

    private Snowflake snowflake = IdUtil.getSnowflake(1, 1);

    @KafkaListener(topics = "seckill_topic", groupId = "seckill-group")
    public void onMessage(String message) {
        System.out.println("Kafka 接收到秒杀成功消息: " + message);

        try {
            // 1. 反序列化消息
            SeckillMessage seckillMessage = JSON.parseObject(message, SeckillMessage.class);

            // 2. 幂等性校验：防止重复消费
            Long count = orderMapper.selectCount(
                    new QueryWrapper<Order>()
                            .eq("user_id", seckillMessage.getUserId())
                            .eq("item_id", seckillMessage.getItemId())
            );
            if (count > 0) {
                System.out.println("重复消息，忽略处理: userId="
                        + seckillMessage.getUserId() + " itemId=" + seckillMessage.getItemId());
                return;
            }

            // 3. 使用雪花算法生成全局唯一订单ID
            Order order = new Order();
            order.setId(snowflake.nextId());
            order.setUserId(seckillMessage.getUserId());
            order.setItemId(seckillMessage.getItemId());
            order.setStatus(0); // 待支付

            // 4. 插入数据库
            orderMapper.insert(order);
            System.out.println("雪花ID订单入库成功，订单号: " + order.getId());

        } catch (Exception e) {
            System.err.println("消费订单消息失败: " + e.getMessage());
        }
    }
}