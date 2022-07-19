package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.filter.UserFilter;
import com.imooc.mall.model.vo.CartVO;
import com.imooc.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车模块
 */
@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {
    @Autowired
    private CartService cartService;

    @ApiOperation("购物车列表")
    @GetMapping("/list")
    public ApiRestResponse list(){
        // 内部获取用户id 防止横向越权
        List<CartVO> cartList = cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(cartList);
    }

    @ApiOperation("购物车添加商品")
    @PostMapping("/add")
    public ApiRestResponse add(@RequestParam("productId") Integer productId, @RequestParam("count") Integer count){

        List<CartVO> cartVOS = cartService.add(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("更新购物车添加商品")
    @PostMapping("/update")
    public ApiRestResponse update(@RequestParam("productId") Integer productId, @RequestParam("count") Integer count){
        List<CartVO> cartVOS = cartService.update(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("删除购物车添加商品")
    @PostMapping("/delete")
    public ApiRestResponse update(@RequestParam("productId") Integer productId){
        // 不能传入userId 和 cart id 否则可以删除别人的购物车
        List<CartVO> cartVOS = cartService.delete(UserFilter.currentUser.getId(), productId);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("选中/不选中购物车添加商品")
    @PostMapping("/select")
    public ApiRestResponse select(@RequestParam("productId") Integer productId, @RequestParam("selected") Integer selected){
        List<CartVO> cartVOS = cartService.selectOrNot(UserFilter.currentUser.getId(), productId, selected);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("全选中/不选中购物车添加商品")
    @PostMapping("/selectAll")
    public ApiRestResponse selectAll(@RequestParam("selected") Integer selected){
        List<CartVO> cartVOS = cartService.selectAllOrNot(UserFilter.currentUser.getId(), selected);
        return ApiRestResponse.success(cartVOS);
    }
}
