package com.imooc.mall.service;

import com.imooc.mall.model.vo.CartVO;

import java.util.List;

public interface CartService {
    List<CartVO> add(Integer userId, Integer productId, Integer count);

    List<CartVO> list(Integer userId);

    List<CartVO> update(Integer userId, Integer productId, Integer count);

    // 删除购物车商品
    List<CartVO> delete(Integer userId, Integer productId);

    // 选中和取消选择
    List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected);

    // 全选或全不选
    List<CartVO> selectAllOrNot(Integer userId, Integer selected);
}
