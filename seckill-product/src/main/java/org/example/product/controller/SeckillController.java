package org.example.product.controller;

import org.example.product.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @PostMapping("/doSeckill")
    public String seckill(@RequestParam Long itemId) {
        // 实际开发从 Token 获取，作业中可以先 mock 一个 userId
        Long userId = (long) (Math.random() * 1000);
        return seckillService.doSeckill(userId, itemId);
    }
}