package com.imooc.mall.model.dao;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.query.ProductListQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    // 通过名称来查找商品
    Product selectByName(String name);

    // 批量上下架
    int batchUpdateSellStatus(@Param("ids") Integer[] ids, @Param("sellStatus") Integer sellStatus);

    List<Product> selectListForAdmin();
    List<Product> selectList(@Param("query")ProductListQuery productListQuery);

}