package com.shixi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixi.common.PageResult;
import com.shixi.common.Result;
import com.shixi.dto.OrderDTO;
import com.shixi.entity.Order;
import com.shixi.service.OrderService;
import com.shixi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer")
public class OrderController {
    
    private final OrderService orderService;
    private final UserService userService;

    @Operation(summary = "分页查询我的订单")
    @GetMapping
    public Result<PageResult<Order>> page(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = userService.getCurrentUserId();
        Page<Order> page = orderService.page(userId, status, current, size);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    @Operation(summary = "查询订单详情")
    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Order> create(@Valid @RequestBody OrderDTO dto) {
        return Result.success(orderService.create(dto));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return Result.success();
    }

    @Operation(summary = "支付订单")
    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        orderService.pay(id);
        return Result.success();
    }

    @Operation(summary = "确认收货")
    @PostMapping("/{id}/receive")
    public Result<Void> receive(@PathVariable Long id) {
        orderService.receive(id);
        return Result.success();
    }

    @Operation(summary = "发货")
    @PostMapping("/{id}/delivery")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delivery(@PathVariable Long id) {
        orderService.delivery(id);
        return Result.success();
    }

    @Operation(summary = "管理员查询所有订单")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<Order>> adminPage(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<Order> page = orderService.page(userId, status, current, size);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }
}
