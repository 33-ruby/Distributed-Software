package org.example.seckillorder.controller;

import org.example.seckillorder.dto.OrderMessageDTO;
import org.example.seckillorder.entity.Order;
import org.example.seckillorder.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private SeckillOrderService orderService;

    // 秒杀下单
    @PostMapping("/seckill")
    public String seckill(@RequestBody OrderMessageDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }

    // 按订单ID查询
    @GetMapping("/{orderId}")
    public Order getByOrderId(@PathVariable Long orderId) {
        return orderService.getByOrderId(orderId);
    }

    // 按用户ID查询
    @GetMapping("/user/{userId}")
    public List<Order> getByUserId(@PathVariable Long userId) {
        return orderService.getByUserId(userId);
    }
}