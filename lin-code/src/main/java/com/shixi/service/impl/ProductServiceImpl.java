package com.shixi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixi.common.BusinessException;
import com.shixi.common.ErrorCode;
import com.shixi.dto.ProductDTO;
import com.shixi.entity.Product;
import com.shixi.mapper.ProductMapper;
import com.shixi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 商品服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductMapper productMapper;

    @Override
    public Page<Product> page(String name, Integer status, Long current, Long size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Product::getName, name)
               .eq(status != null, Product::getStatus, status)
               .orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return product;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ProductDTO dto) {
        Product product = new Product();
        BeanUtil.copyProperties(dto, product);
        product.setSales(0);
        productMapper.insert(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ProductDTO dto) {
        Product product = getById(id);
        BeanUtil.copyProperties(dto, product);
        productMapper.updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        productMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if (product.getStock() < quantity) {
            throw new BusinessException(ErrorCode.PRODUCT_STOCK_NOT_ENOUGH);
        }
        product.setStock(product.getStock() - quantity);
        product.setSales(product.getSales() + quantity);
        productMapper.updateById(product);
    }
}
