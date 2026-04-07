package com.shixi.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixi.BaseTest;
import com.shixi.common.BusinessException;
import com.shixi.common.ErrorCode;
import com.shixi.dto.ProductDTO;
import com.shixi.entity.Product;
import com.shixi.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 商品服务单元测试
 */
@DisplayName("商品服务测试")
class ProductServiceImplTest extends BaseTest {

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("分页查询商品-无筛选条件")
    void page_NoFilter() {
        // when
        Page<Product> page = productService.page(null, null, 1L, 10L);

        // then
        assertNotNull(page);
        assertTrue(page.getTotal() > 0);
        assertEquals(3, page.getRecords().size()); // init.sql 中插入了3个商品
    }

    @Test
    @DisplayName("分页查询商品-按名称筛选")
    void page_FilterByName() {
        // when
        Page<Product> page = productService.page("测试商品1", null, 1L, 10L);

        // then
        assertNotNull(page);
        assertEquals(1, page.getTotal());
        assertEquals("测试商品1", page.getRecords().get(0).getName());
    }

    @Test
    @DisplayName("分页查询商品-按状态筛选")
    void page_FilterByStatus() {
        // when
        Page<Product> page = productService.page(null, 1, 1L, 10L);

        // then
        assertNotNull(page);
        assertTrue(page.getTotal() > 0);
    }

    @Test
    @DisplayName("查询商品详情-成功")
    void getById_Success() {
        // when
        Product product = productService.getById(1L);

        // then
        assertNotNull(product);
        assertEquals("测试商品1", product.getName());
        assertEquals(new BigDecimal("100.00"), product.getPrice());
    }

    @Test
    @DisplayName("查询商品详情-不存在抛出异常")
    void getById_NotFound() {
        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.getById(9999L));
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("创建商品-成功")
    void create_Success() {
        // given
        ProductDTO dto = new ProductDTO();
        dto.setName("新商品");
        dto.setDescription("商品描述");
        dto.setPrice(new BigDecimal("299.00"));
        dto.setOriginalPrice(new BigDecimal("399.00"));
        dto.setStock(100);
        dto.setStatus(1);

        // when
        productService.create(dto);

        // then
        Product product = productService.getById(4L); // 新增的商品ID为4
        assertNotNull(product);
        assertEquals("新商品", product.getName());
        assertEquals(new BigDecimal("299.00"), product.getPrice());
        assertEquals(100, product.getStock());
        assertEquals(0, product.getSales()); // 新商品销量应为0
    }

    @Test
    @DisplayName("更新商品-成功")
    void update_Success() {
        // given
        ProductDTO dto = new ProductDTO();
        dto.setName("更新后的商品");
        dto.setPrice(new BigDecimal("150.00"));
        dto.setStock(200);
        dto.setStatus(0);

        // when
        productService.update(1L, dto);

        // then
        Product product = productService.getById(1L);
        assertEquals("更新后的商品", product.getName());
        assertEquals(new BigDecimal("150.00"), product.getPrice());
        assertEquals(200, product.getStock());
        assertEquals(0, product.getStatus());
    }

    @Test
    @DisplayName("删除商品-成功")
    void delete_Success() {
        // when
        productService.delete(1L);

        // then
        // MyBatis-Plus 逻辑删除，实际是更新 deleted=1
        Product product = productService.getById(1L);
        assertNull(product); // 逻辑删除后查不到
    }

    @Test
    @DisplayName("扣减库存-成功")
    void deductStock_Success() {
        // given
        Long productId = 1L;
        Integer quantity = 10;
        Product before = productService.getById(productId);
        int expectedStock = before.getStock() - quantity;
        int expectedSales = before.getSales() + quantity;

        // when
        productService.deductStock(productId, quantity);

        // then
        Product after = productService.getById(productId);
        assertEquals(expectedStock, after.getStock());
        assertEquals(expectedSales, after.getSales());
    }

    @Test
    @DisplayName("扣减库存-库存不足")
    void deductStock_StockNotEnough() {
        // given
        Long productId = 1L;
        Integer quantity = 9999; // 超过库存

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.deductStock(productId, quantity));
        assertEquals(ErrorCode.PRODUCT_STOCK_NOT_ENOUGH.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("扣减库存-商品不存在")
    void deductStock_ProductNotFound() {
        // given
        Long productId = 9999L;
        Integer quantity = 10;

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.deductStock(productId, quantity));
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND.getCode(), exception.getCode());
    }
}
