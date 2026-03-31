package org.example.product.controller;

import org.example.common.Result;
import org.example.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public Result getProduct(@PathVariable("id") Long id) {
        System.out.println("收到请求，商品ID：" + id);
        return Result.success(productService.getProductDetail(id));
    }
}