package org.example.stock.consumer;

import com.alibaba.fastjson.JSON;
import org.example.common.dto.SeckillMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PayConsumer {

    @KafkaListener(topics = "pay_topic", groupId = "stock-group")
    public void onMessage(String message) {
        System.out.println("收到支付成功消息: " + message);
        try {
            SeckillMessage msg = JSON.parseObject(message, SeckillMessage.class);
            // 这里可以做：记账、通知、积分等后续处理
            // 目前打印日志证明消息链路通畅
            System.out.println("支付后续处理完成，userId=" + msg.getUserId()
                    + " itemId=" + msg.getItemId());
        } catch (Exception e) {
            System.err.println("支付消息处理失败: " + e.getMessage());
        }
    }
}