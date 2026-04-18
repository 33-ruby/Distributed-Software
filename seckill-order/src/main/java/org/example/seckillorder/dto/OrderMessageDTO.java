package org.example.seckillorder.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderMessageDTO implements Serializable {
    private Long userId;
    private Long productId;
    private Integer count;
}
