package com.imooc.mall.model.dao;

import com.imooc.mall.model.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNo(@Param("orderNo") String orderNo);

    List<Order> selectForCustomer(@Param("userId") Integer userId);
    List<Order> selectAllForAdmin();
}