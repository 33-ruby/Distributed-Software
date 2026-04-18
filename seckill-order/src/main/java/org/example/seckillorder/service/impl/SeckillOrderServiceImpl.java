package org.example.seckillorder.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.common.dto.SeckillMessage;
import org.example.seckillorder.dto.OrderMessageDTO;
import org.example.seckillorder.entity.Order;
import org.example.seckillorder.mapper.OrderMapper;
import org.example.seckillorder.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String STOCK_KEY = "seckill:stock:";
    private static final String SECKILL_TOPIC = "seckill_topic";

    @Override
    public String createOrder(OrderMessageDTO orderDTO) {
        Long userId = orderDTO.getUserId();
        Long itemId = orderDTO.getProductId();

        // 1. 幂等性校验：同一用户同一商品只能下一单
        Long count = orderMapper.selectCount(
                new QueryWrapper<Order>()
                        .eq("user_id", userId)
                        .eq("goods_id", itemId)
        );
        if (count > 0) {
            return "您已参与过该商品的秒杀，请勿重复下单";
        }

        // 2. Redis 预减库存
        String stockKey = STOCK_KEY + itemId;
        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        if (stock == null || stock < 0) {
            redisTemplate.opsForValue().increment(stockKey);
            return "库存不足，秒杀失败";
        }

        // 3. 发送 Kafka 消息，异步创建订单
        SeckillMessage msg = new SeckillMessage();
        msg.setUserId(userId);
        msg.setItemId(itemId);
        kafkaTemplate.send(SECKILL_TOPIC, JSON.toJSONString(msg));

        return "秒杀请求已提交，订单处理中";
    }

    @Override
    public Order getByOrderId(Long orderId) {
        return orderMapper.selectById(orderId);
    }

    @Override
    public List<Order> getByUserId(Long userId) {
        return orderMapper.selectList(
                new QueryWrapper<Order>().eq("user_id", userId)
        );
    }

    @Override
    public String payOrder(Long orderId) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return "订单不存在";
        }
        if (order.getStatus() != 0) {
            return "订单状态异常，当前状态: " + order.getStatus();
        }

        // 2. 更新订单状态为已支付
        order.setStatus(1);
        int rows = orderMapper.updateById(order);
        if (rows <= 0) {
            return "订单状态更新失败";
        }

        // 3. 发送支付成功消息，保证后续处理最终一致
        SeckillMessage msg = new SeckillMessage();
        msg.setUserId(order.getUserId());
        msg.setItemId(order.getGoodsId());
        kafkaTemplate.send("pay_topic", JSON.toJSONString(msg));
        System.out.println("支付成功消息已发送，orderId=" + orderId);

        return "支付成功，订单号: " + orderId;
    }
}