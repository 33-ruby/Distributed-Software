package org.example.seckillorder.service;

import org.example.seckillorder.dto.OrderMessageDTO;
import org.example.seckillorder.entity.Order;
import java.util.List;

public interface SeckillOrderService {
    // 秒杀主逻辑：预减库存、幂等检查、发消息
    String createOrder(OrderMessageDTO orderDTO);

    // 按订单ID查询
    Order getByOrderId(Long orderId);

    // 按用户ID查询
    List<Order> getByUserId(Long userId);

    // 支付接口
    String payOrder(Long orderId);
}