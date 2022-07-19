package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.model.request.CreateOrderReq;
import com.imooc.mall.model.vo.OrderVO;
import com.imooc.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping("/order/create")
    @ApiOperation("前台创建订单")
    public ApiRestResponse create(@RequestBody CreateOrderReq createOrderReq){
        String orderNo = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @GetMapping("/order/detail")
    @ApiOperation("前台订单详情")
    public ApiRestResponse detail(@RequestParam("orderNo") String orderNo){
        OrderVO detail = orderService.detail(orderNo);
        return ApiRestResponse.success(detail);
    }

    @GetMapping("/order/list")
    @ApiOperation("前台订单列表")
    public ApiRestResponse list(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        PageInfo pageInfo = orderService.listForCustomer(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @PostMapping("/order/cancel")
    @ApiOperation("前台取消订单")
    public ApiRestResponse cancel(@RequestParam("orderNo") String orderNo){
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    @GetMapping("/order/qrcode")
    @ApiOperation("生成支付二维码")
    public ApiRestResponse qrcode(@RequestParam("orderNo") String orderNo){
        String pngAddr = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddr);
    }

    @GetMapping("/pay")
    @ApiOperation("支付接口")
    public ApiRestResponse pay(@RequestParam("orderNo") String orderNo){
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }

    @PostMapping("/order/finished")
    @ApiOperation("完结订单")
    public ApiRestResponse finished(@RequestParam("orderNo") String orderNo){
        orderService.finish(orderNo);
        return ApiRestResponse.success();
    }
}
