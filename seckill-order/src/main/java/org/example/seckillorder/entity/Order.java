package org.example.seckillorder.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("t_order") // 对应数据库表名
public class Order {
    @TableId // 主键
    private Long id;   // 雪花算法生成的ID
    private Long userId;
    private Long itemId;
    private Integer status; // 0-待支付
    private Date createTime;
}