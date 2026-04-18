package org.example.seckillorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.seckillorder.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    // 继承 BaseMapper 后，insert/select 等方法已自动具备
}