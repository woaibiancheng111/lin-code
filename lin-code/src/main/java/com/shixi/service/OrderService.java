package com.shixi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixi.dto.OrderDTO;
import com.shixi.entity.Order;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 分页查询订单
     */
    Page<Order> page(Long userId, Integer status, Long current, Long size);
    
    /**
     * 根据ID查询订单
     */
    Order getById(Long id);
    
    /**
     * 创建订单
     */
    Order create(OrderDTO dto);
    
    /**
     * 取消订单
     */
    void cancel(Long id);
    
    /**
     * 支付订单
     */
    void pay(Long id);
    
    /**
     * 发货
     */
    void delivery(Long id);
    
    /**
     * 确认收货
     */
    void receive(Long id);
}
