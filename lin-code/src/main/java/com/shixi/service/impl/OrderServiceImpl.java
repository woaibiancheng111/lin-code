package com.shixi.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixi.common.BusinessException;
import com.shixi.common.ErrorCode;
import com.shixi.dto.OrderDTO;
import com.shixi.entity.Order;
import com.shixi.entity.OrderItem;
import com.shixi.entity.Product;
import com.shixi.mapper.OrderItemMapper;
import com.shixi.mapper.OrderMapper;
import com.shixi.mq.OrderMessageProducer;
import com.shixi.service.OrderService;
import com.shixi.service.ProductService;
import com.shixi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductService productService;
    private final UserService userService;
    private final OrderMessageProducer orderMessageProducer;

    @Override
    public Page<Order> page(Long userId, Integer status, Long current, Long size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, Order::getUserId, userId)
               .eq(status != null, Order::getStatus, status)
               .orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public Order getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order create(OrderDTO dto) {
        Long userId = userService.getCurrentUserId();
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setStatus(0); // 待支付
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverAddress(dto.getReceiverAddress());
        order.setRemark(dto.getRemark());
        order.setFreightAmount(BigDecimal.ZERO);
        
        // 处理订单明细
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDTO.OrderItemDTO> items = dto.getItems();
        
        for (OrderDTO.OrderItemDTO item : items) {
            Product product = productService.getById(item.getProductId());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            
            totalAmount = totalAmount.add(orderItem.getTotalAmount());
        }
        
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount.add(order.getFreightAmount()));
        
        // 保存订单
        orderMapper.insert(order);
        
        // 保存订单明细
        for (OrderDTO.OrderItemDTO item : items) {
            Product product = productService.getById(item.getProductId());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            
            orderItemMapper.insert(orderItem);
        }
        
        // 发送订单创建消息（异步扣减库存）
        orderMessageProducer.sendOrderCreatedMessage(order.getId());
        
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        Order order = checkOrderOwner(id);
        if (order.getStatus() != 0) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(4); // 已取消
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Long id) {
        Order order = checkOrderOwner(id);
        if (order.getStatus() != 0) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(1); // 已支付
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delivery(Long id) {
        Order order = getById(id);
        if (order.getStatus() != 1) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(2); // 已发货
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receive(Long id) {
        Order order = checkOrderOwner(id);
        if (order.getStatus() != 2) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(3); // 已完成
        order.setReceiveTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }
    
    private Order checkOrderOwner(Long id) {
        Order order = getById(id);
        Long userId = userService.getCurrentUserId();
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return order;
    }
}
