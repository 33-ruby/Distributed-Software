package org.example.product.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_goods")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String goodsName;
    private BigDecimal goodsPrice;
    private Integer goodsStock;
    private String description;
    private Date createDate;
}