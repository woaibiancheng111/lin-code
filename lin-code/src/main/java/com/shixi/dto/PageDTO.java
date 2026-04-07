package com.shixi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页请求DTO
 */
@Data
@Schema(description = "分页请求")
public class PageDTO {
    
    @Schema(description = "当前页码", defaultValue = "1")
    private Long current = 1L;
    
    @Schema(description = "每页大小", defaultValue = "10")
    private Long size = 10L;
    
    @Schema(description = "排序字段")
    private String orderBy;
    
    @Schema(description = "是否升序", defaultValue = "true")
    private Boolean asc = true;
}
