package com.shixi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品请求DTO
 */
@Data
@Schema(description = "商品请求")
public class ProductDTO {
    
    @Schema(description = "商品名称", required = true)
    @NotBlank(message = "商品名称不能为空")
    private String name;
    
    @Schema(description = "商品描述")
    private String description;
    
    @Schema(description = "价格", required = true)
    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能小于0")
    private BigDecimal price;
    
    @Schema(description = "原价")
    private BigDecimal originalPrice;
    
    @Schema(description = "库存", required = true)
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock;
    
    @Schema(description = "图片")
    private String image;
    
    @Schema(description = "分类ID")
    private Long categoryId;
    
    @Schema(description = "状态 0-下架 1-上架")
    private Integer status;
}
