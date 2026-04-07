package com.shixi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shixi.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
