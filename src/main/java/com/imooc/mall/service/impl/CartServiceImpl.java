package com.imooc.mall.service.impl;

import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMailExceptionEnum;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.model.dao.CartMapper;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.pojo.Cart;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.vo.CartVO;
import com.imooc.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车service实现类
 */
@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count){
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart == null){
            // 商品不在购物车里, 需要新增记录
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.insertSelective(cart);
        } else { // 商品已经在购物车里数量相加
            count += cart.getQuantity();
            Cart newCart = new Cart();
            newCart.setQuantity(count);
            newCart.setId(cart.getId());
            newCart.setUserId(cart.getUserId());
            newCart.setProductId(cart.getProductId());
            newCart.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(newCart);
        }
        return this.list(userId);
    }

    // 购物车列表
    @Override
    public List<CartVO> list(Integer userId){
        List<CartVO> cartVOS = cartMapper.selectList(userId);
        for(CartVO cartVO : cartVOS){
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOS;
    }

    // 更新购物车
    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count){
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart == null){
            // 这个商品之前不在购物车, 无法更新
            throw new ImoocMallException(ImoocMailExceptionEnum.UPDATE_FAILED);
        } else { // 更新count
            Cart newCart = new Cart();
            newCart.setQuantity(count);
            newCart.setId(cart.getId());
            newCart.setUserId(cart.getUserId());
            newCart.setProductId(cart.getProductId());
            newCart.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(newCart);
        }
        return this.list(userId);
    }

    // 删除购物车商品
    @Override
    public List<CartVO> delete(Integer userId, Integer productId){
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart == null){
            // 不能删除没有的
            throw new ImoocMallException(ImoocMailExceptionEnum.DELETE_FAILED);
        } else {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }

    // 选中和取消选择
    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected){
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart == null) {
            throw new ImoocMallException(ImoocMailExceptionEnum.UPDATE_FAILED);
        }else {
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);
    }

    // 全选或全不选
    @Override
    public List<CartVO> selectAllOrNot(Integer userId, Integer selected){
        // 改变选中状态
        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);
    }


    // 验证参数是否合法
    private void validProduct(Integer productId, Integer count) {
        Product product = productMapper.selectByPrimaryKey(productId);
        // 判断商品是否存在和上架
        if(product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)){
            throw new ImoocMallException(ImoocMailExceptionEnum.NOT_SALE);
        }
        // 判断商品库存
        if(count > product.getStock()){
            throw new ImoocMallException(ImoocMailExceptionEnum.NOT_ENOUGH);
        }
    }
}
