package org.example.seckillorder.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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

    // 秒杀下单：限流 + 熔断
    @PostMapping("/seckill")
    @RateLimiter(name = "seckillOrder", fallbackMethod = "rateLimitFallback")
    @CircuitBreaker(name = "seckillOrder", fallbackMethod = "circuitBreakerFallback")
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

    // 订单支付
    @PostMapping("/pay/{orderId}")
    public String pay(@PathVariable Long orderId) {
        return orderService.payOrder(orderId);
    }

    // 限流降级方法
    public String rateLimitFallback(@RequestBody OrderMessageDTO orderDTO, Throwable t) {
        return "系统繁忙，请稍后再试（限流）";
    }

    // 熔断降级方法
    public String circuitBreakerFallback(@RequestBody OrderMessageDTO orderDTO, Throwable t) {
        return "服务暂时不可用，请稍后再试（熔断）";
    }
}