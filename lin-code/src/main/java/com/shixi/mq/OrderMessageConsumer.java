package com.shixi.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shixi.config.RabbitMQConfig;
import com.shixi.entity.OrderItem;
import com.shixi.mapper.OrderItemMapper;
import com.shixi.service.ProductService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 订单消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageConsumer {

    private final OrderItemMapper orderItemMapper;
    private final ProductService productService;

    /**
     * 消费订单创建消息 - 扣减库存
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderCreated(Long orderId, Channel channel, org.springframework.amqp.core.Message message) throws IOException {
        log.info("消费订单创建消息: {}", orderId);
        try {
            // 查询订单明细
            List<OrderItem> orderItems = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
            );
            
            // 扣减库存
            for (OrderItem item : orderItems) {
                productService.deductStock(item.getProductId(), item.getQuantity());
            }
            
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("订单库存扣减成功: {}", orderId);
        } catch (Exception e) {
            log.error("订单库存扣减失败: {}", orderId, e);
            // 拒绝消息，重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
