package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class OrderAdminController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/order/list")
    @ApiOperation("管理员订单列表")
    public ApiRestResponse listForAdmin(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        PageInfo pageInfo = orderService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @PostMapping("/order/delivered")
    @ApiOperation("管理员发货")
    public ApiRestResponse delivered(@RequestParam("orderNo") String orderNo){
        orderService.deliver(orderNo);
        return ApiRestResponse.success();
    }

}
