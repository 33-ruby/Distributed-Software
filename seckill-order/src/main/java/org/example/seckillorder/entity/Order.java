package org.example.seckillorder.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_order")
public class Order {
    @TableId
    private Long id;
    private Long userId;
    private Long seckillGoodsId;
    private Long goodsId;
    private String goodsName;
    private BigDecimal seckillPrice;
    private Integer quantity;
    private Integer status; // 0-待支付 1-已支付 2-已取消
    private Date createDate;
}