package com.shixi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shixi.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细Mapper
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
