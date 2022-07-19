package com.imooc.mall.model.dao;

import com.imooc.mall.model.pojo.Cart;
import com.imooc.mall.model.vo.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<CartVO> selectList(Integer userId);

    int selectOrNot(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("selected") Integer selected);
}