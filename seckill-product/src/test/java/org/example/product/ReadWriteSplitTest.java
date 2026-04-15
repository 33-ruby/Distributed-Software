package org.example.product;

import org.example.product.datasource.DataSourceContextHolder;
import org.example.product.pojo.Product;
import org.example.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;

@ActiveProfiles("local")  // 使用 application-local.yml
@SpringBootTest
public class ReadWriteSplitTest {

    @Autowired
    private ProductService productService;

    /**
     * 测试读操作 -> 应走从库(slave)
     * getProductDetail 以 get 开头，AOP 会切换到从库
     */
    @Test
    void testRead_shouldGoToSlave() {
        System.out.println("\n========== 测试读操作 ==========");
        Product product = productService.getProductDetail(1L);
        System.out.println("查询结果: " + product);
        System.out.println("========== 读操作结束 ==========\n");
    }

    /**
     * 测试写操作 -> 应走主库(master)
     * saveProduct 以 save 开头，AOP 会切换到主库
     */
    @Test
    void testWrite_shouldGoToMaster() {
        System.out.println("\n========== 测试写操作 ==========");
        Product p = new Product();
        p.setGoodsName("读写分离测试商品");
        p.setGoodsPrice(new BigDecimal("199.00"));
        p.setGoodsStock(50);
        p.setDescription("AOP读写分离验证");

        Product saved = productService.saveProduct(p);
        System.out.println("插入结果: " + saved);
        System.out.println("========== 写操作结束 ==========\n");
    }

    /**
     * 连续测试读写，观察数据源切换
     */
    @Test
    void testReadWriteSwitch() {
        System.out.println("\n========== 连续读写切换测试 ==========");

        System.out.println("--- 第1次：读操作 ---");
        productService.getProductDetail(1L);

        System.out.println("--- 第2次：写操作 ---");
        Product p = new Product();
        p.setGoodsName("切换测试商品");
        p.setGoodsPrice(new BigDecimal("99.00"));
        p.setGoodsStock(10);
        p.setDescription("测试读写切换");
        productService.saveProduct(p);

        System.out.println("--- 第3次：读操作 ---");
        productService.getProductDetail(1L);

        System.out.println("========== 切换测试结束 ==========\n");
    }
}