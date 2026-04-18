package org.example.stock.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.common.dto.SeckillMessage;
import org.example.stock.entity.SeckillGoods;
import org.example.stock.mapper.SeckillGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StockConsumer {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @KafkaListener(topics = "stock_deduct_topic", groupId = "stock-group")
    public void onMessage(String message) {
        System.out.println("库存服务收到扣减消息: " + message);
        try {
            SeckillMessage msg = JSON.parseObject(message, SeckillMessage.class);

            // 查询库存记录
            SeckillGoods goods = seckillGoodsMapper.selectOne(
                    new QueryWrapper<SeckillGoods>().eq("goods_id", msg.getItemId())
            );

            if (goods == null || goods.getStockCount() <= 0) {
                System.out.println("库存不足或商品不存在，忽略扣减: itemId=" + msg.getItemId());
                return;
            }

            // 乐观锁扣减库存（version 字段自动处理）
            goods.setStockCount(goods.getStockCount() - 1);
            int rows = seckillGoodsMapper.updateById(goods);

            if (rows > 0) {
                System.out.println("DB库存扣减成功，itemId=" + msg.getItemId()
                        + " 剩余库存=" + goods.getStockCount());
            } else {
                System.out.println("乐观锁冲突，扣减失败，itemId=" + msg.getItemId());
                // 实际生产中这里需要重试
            }

        } catch (Exception e) {
            System.err.println("库存扣减消息处理失败: " + e.getMessage());
        }
    }
}