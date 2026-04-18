package org.example.product.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.example.product.mapper.ProductMapper;
import org.example.product.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY = "product:detail:";
    private static final String EMPTY_VALUE = "EMPTY";

    @DS("slave")  // 读操作走从库
    public Product getProductDetail(Long id) {
        String key = CACHE_KEY + id;

        Object cached = redisTemplate.opsForValue().get(key);

        if (EMPTY_VALUE.equals(cached)) {
            return null;
        }
        if (cached != null) {
            return (Product) cached;
        }

        String lockKey = "product:lock:" + id;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(locked)) {
            try {
                Object cachedAgain = redisTemplate.opsForValue().get(key);
                if (EMPTY_VALUE.equals(cachedAgain)) {
                    return null;
                }
                if (cachedAgain != null) {
                    return (Product) cachedAgain;
                }

                Product product = productMapper.selectById(id);
                log.info(">>> 查询数据库 productId={}", id);
                if (product != null) {
                    long expire = 30 * 60 + (long) (Math.random() * 5 * 60);
                    redisTemplate.opsForValue().set(key, product, expire, TimeUnit.SECONDS);
                } else {
                    redisTemplate.opsForValue().set(key, EMPTY_VALUE, 60, TimeUnit.SECONDS);
                }
                return product;

            } finally {
                redisTemplate.delete(lockKey);
            }
        } else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return getProductDetail(id);
        }
    }

    @DS("master")  // 写操作走主库
    public Product saveProduct(Product product) {
        productMapper.insert(product);
        log.info(">>> 插入商品: {}", product.getGoodsName());
        return product;
    }
}