package com.shixi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单请求DTO
 */
@Data
@Schema(description = "订单请求")
public class OrderDTO {
    
    @Schema(description = "订单明细", required = true)
    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<OrderItemDTO> items;
    
    @Schema(description = "收货人姓名", required = true)
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;
    
    @Schema(description = "收货人电话", required = true)
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;
    
    @Schema(description = "收货地址", required = true)
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;
    
    @Schema(description = "备注")
    private String remark;

    @Data
    @Schema(description = "订单明细")
    public static class OrderItemDTO {
        
        @Schema(description = "商品ID", required = true)
        @NotNull(message = "商品ID不能为空")
        private Long productId;
        
        @Schema(description = "数量", required = true)
        @NotNull(message = "数量不能为空")
        private Integer quantity;
    }
}
