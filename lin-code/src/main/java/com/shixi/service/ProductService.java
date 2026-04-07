package com.shixi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixi.dto.ProductDTO;
import com.shixi.entity.Product;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 分页查询商品
     */
    Page<Product> page(String name, Integer status, Long current, Long size);
    
    /**
     * 根据ID查询商品
     */
    Product getById(Long id);
    
    /**
     * 创建商品
     */
    void create(ProductDTO dto);
    
    /**
     * 更新商品
     */
    void update(Long id, ProductDTO dto);
    
    /**
     * 删除商品
     */
    void delete(Long id);
    
    /**
     * 扣减库存
     */
    void deductStock(Long productId, Integer quantity);
}
